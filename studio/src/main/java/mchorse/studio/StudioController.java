package mchorse.studio;

import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.input.IJoystickHandler;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.core.input.JoystickInput;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.studio.camera.StudioCameraController;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.function.Supplier;

public class StudioController implements ITickable, IMouseHandler, IKeyHandler, IJoystickHandler
{
    public StudioEngine engine;

    public Entity controller;
    public Entity player;
    public boolean creative;

    /* ... */
    public Keybind walkForward;
    public Keybind walkBackward;
    public Keybind walkLeft;
    public Keybind walkRight;

    public boolean back = true;
    public boolean firstPerson;

    private StudioCameraController camera = new StudioCameraController(this);

    private int lastX;
    private int lastY;

    private final Vector3f direction = new Vector3f();
    private boolean joystickControl;
    private Vector2f joystickDirection = new Vector2f();
    private Vector2f joystickLooking = new Vector2f();

    private int mouseMode;
    private Vector2f mouseStick = new Vector2f();

    public StudioController(StudioEngine engine)
    {
        this.engine = engine;

        /* General keybinds */
        Supplier<Boolean> isMenuNotPresent = () -> this.engine.get(IBridgeMenu.class).getCurrentMenu() == null;

        KeybindCategory general = new KeybindCategory("general").active(isMenuNotPresent);

        Keybind pause = new Keybind("pause").onPress(this::pause);

        general.add(pause.keys(GLFW.GLFW_KEY_ESCAPE));

        engine.keys.keybinds.add(general);

        /* Movement keybinds */
        KeybindCategory movement = new KeybindCategory("movement").active(isMenuNotPresent);

        Keybind jump = new Keybind("jump").onPress(this::jump);
        Keybind sneak = new Keybind("sneak", this::sneak);
        Keybind back = new Keybind("back", () -> this.back = !this.back);
        Keybind mouseMode = new Keybind("control_mode", () ->
        {
            this.mouseMode += 1;

            Entity controller = this.getController();

            if (this.mouseMode % 4 != 0 && controller != null && controller.has(PlayerComponent.class))
            {
                int index = this.mouseMode % 4 - 1;
                PlayerComponent component = controller.get(PlayerComponent.class);

                this.mouseStick.set(component.sticks[index * 2 + 1], component.sticks[index * 2]);
            }
        });

        movement.add(this.walkForward = new Keybind("walk_forward").keys(GLFW.GLFW_KEY_W));
        movement.add(this.walkBackward = new Keybind("walk_backward").keys(GLFW.GLFW_KEY_S));
        movement.add(this.walkLeft = new Keybind("walk_left").keys(GLFW.GLFW_KEY_A));
        movement.add(this.walkRight = new Keybind("walk_right").keys(GLFW.GLFW_KEY_D));
        movement.add(sneak.keys(GLFW.GLFW_KEY_LEFT_SHIFT));
        movement.add(jump.keys(GLFW.GLFW_KEY_SPACE));
        movement.add(back.keys(GLFW.GLFW_KEY_F5));
        movement.add(mouseMode.keys(GLFW.GLFW_KEY_Q));

        engine.keys.keybinds.add(general);
        engine.keys.keybinds.add(movement);
        engine.cameraController.add(this.camera);
    }

    /* Callbacks */

    private void pause()
    {
        this.engine.screen.pause();
    }

    private void jump()
    {
        if (!this.canControl())
        {
            return;
        }

        Entity controller = this.engine.get(IBridgePlayer.class).getController();

        controller.basic.velocity.y = controller.basic.sneak ? 0.4F : 0.5F;
        controller.basic.velocity.x *= 1.2F;
        controller.basic.velocity.z *= 1.2F;
        controller.basic.grounded = false;
    }

    private void sneak(Boolean sneak)
    {
        if (!this.canControl())
        {
            return;
        }

        Entity controller = this.engine.get(IBridgePlayer.class).getController();

        if (controller != null)
        {
            controller.basic.sneak = !sneak;
        }
    }


    /* Getters/setters */

    public boolean canControl()
    {
        return true;
    }

    public Entity getController()
    {
        return this.controller == null ? this.player : this.controller;
    }

    public void setController(Entity entity)
    {
        this.controller = entity;
    }

    public void setCreative(boolean creative)
    {
        this.creative = creative;

        if (creative)
        {
            /* If it's creative, then player should be removed from the world */
            if (this.player != null)
            {
                this.engine.world.removeEntitySafe(this.player);

                this.player = null;
            }
        }
    }

    public void init()
    {
        this.reset();
    }

    public void reload()
    {
        this.creative = this.engine.development;
        this.player = null;
    }

    public void reset()
    {
        MouseInput input = this.engine.mouse;

        this.lastX = input.x;
        this.lastY = input.y;

        this.mouseStick.set(0, 0);
    }

    @Override
    public void update()
    {
        /* Detach the player */
        if (this.player != null && this.player.isRemoved())
        {
            this.player = null;
        }

        if (this.controller != null && this.controller.isRemoved())
        {
            this.controller = null;
        }

        this.engine.world.view.updateChunks(this.engine.cameraController.camera.position);

        Entity controller = this.getController();

        /* Input */
        if (controller != null && this.canControl())
        {
            float moveZ = this.walkForward.isDown() ? -1 : (this.walkBackward.isDown() ? 1 : 0);
            float moveX = this.walkLeft.isDown() ? -1 : (this.walkRight.isDown() ? 1 : 0);

            if (this.joystickControl)
            {
                moveX = this.joystickDirection.x;
                moveZ = this.joystickDirection.y;
            }

            if (moveZ != 0 || moveX != 0)
            {
                if (this.joystickControl)
                {
                    this.direction.set(moveX, 0, moveZ).mul(0.25F);
                }
                else
                {
                    this.direction.set(moveX, 0, moveZ).normalize().mul(0.25F);
                }

                BasicComponent basic = controller.basic;

                Matrices.rotate(this.direction, 0, -basic.rotation.y);
                this.direction.mul((basic.sneak ? 0.33F : 1F) * (basic.grounded ? 1F : 0.05F));
                this.direction.mul(basic.speed);

                basic.velocity.x += this.direction.x;
                basic.velocity.z += this.direction.z;
            }
        }

        JoystickInput joystick = this.engine.joystick;

        if (controller != null)
        {
            if (joystick.isPresent())
            {
                this.handleJoystick(controller, joystick, joystick.getUpdatedState());
            }
            else if (this.mouseMode % 4 != 0)
            {
                PlayerComponent component = controller.get(PlayerComponent.class);

                if (component != null)
                {
                    int index = this.mouseMode % 4 - 1;

                    component.sticks[index * 2] = this.mouseStick.y;
                    component.sticks[index * 2 + 1] = this.mouseStick.x;
                }
            }
        }
    }

    public void render()
    {
        if (this.engine.screen.hasMenu())
        {
            return;
        }

        MouseInput mouse = this.engine.mouse;
        int x = mouse.x;
        int y = mouse.y;
        float sensitivity = 400F;
        Entity controller = this.getController();

        if (this.firstPerson)
        {
            sensitivity *= 2;
        }

        boolean mouseLook = this.mouseMode % 4 == 0;

        if (controller != null && this.canControl())
        {
            if (mouseLook)
            {
                BasicComponent basic = controller.basic;

                float xx = (y - this.lastY) / sensitivity;
                float yy = (x - this.lastX) / sensitivity;

                if (this.joystickControl)
                {
                    xx = this.joystickLooking.y / 25F;
                    yy = this.joystickLooking.x / 25F;
                }

                if (xx != 0 || yy != 0)
                {
                    basic.rotation.x += xx;
                    basic.rotation.y += yy;
                    basic.rotation.x = MathUtils.clamp(basic.rotation.x, -MathUtils.PI / 2, MathUtils.PI / 2);
                    basic.prevRotation.x = basic.rotation.x;
                    basic.prevRotation.y = basic.rotation.y;
                }
            }
            else
            {
                sensitivity = 50F;

                float xx = (y - this.lastY) / sensitivity;
                float yy = (x - this.lastX) / sensitivity;

                this.mouseStick.add(xx, yy);
                this.mouseStick.x = MathUtils.clamp(this.mouseStick.x, -1F, 1F);
                this.mouseStick.y = MathUtils.clamp(this.mouseStick.y, -1F, 1F);
            }
        }

        this.lastY = y;
        this.lastX = x;
    }

    /* IMouseHandler */

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        Entity controller = this.getController();

        if (controller == null || !this.canControl())
        {
            return;
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {}

    /* IKeyHandler */

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        Entity controller = this.getController();

        if (controller == null || this.engine.screen.hasMenu() || !this.canControl())
        {
            return false;
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {}

    @Override
    public boolean handleGamepad(int button, int action)
    {
        if (action != GLFW.GLFW_PRESS)
        {
            return false;
        }

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
        {
            this.joystickControl = !this.joystickControl;

            return true;
        }
        else if (button == GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
        {
            this.back = !this.back;

            return true;
        }

        if (!this.joystickControl)
        {
            return false;
        }

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_A)
        {
            this.jump();

            return true;
        }

        return false;

    }

    protected void handleJoystick(Entity controller, JoystickInput joystick, GLFWGamepadState state)
    {
        PlayerComponent component = controller.get(PlayerComponent.class);

        if (component != null)
        {
            component.updateJoystick(state);
        }

        this.joystickDirection.set(state.axes(0), state.axes(1));
        this.joystickLooking.set(state.axes(2), state.axes(3));

        if (Math.abs(this.joystickDirection.x) < 0.05F) this.joystickDirection.x = 0F;
        if (Math.abs(this.joystickDirection.y) < 0.05F) this.joystickDirection.y = 0F;
        if (Math.abs(this.joystickLooking.x) < 0.05F) this.joystickLooking.x = 0F;
        if (Math.abs(this.joystickLooking.y) < 0.05F) this.joystickLooking.y = 0F;
    }
}