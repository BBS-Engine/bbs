package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.core.input.JoystickInput;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.FormComponent;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

public class ThirdPersonGameController extends BaseGameController
{
    public KeybindCategory movement;
    public Keybind walkForward;
    public Keybind walkBackward;
    public Keybind walkLeft;
    public Keybind walkRight;

    public boolean back = true;
    public boolean firstPerson;

    private ThirdPersonCameraController camera = new ThirdPersonCameraController(this);

    private int lastX;
    private int lastY;

    private final Vector3f direction = new Vector3f();
    private boolean joystickControl;
    private Vector2f joystickDirection = new Vector2f();
    private Vector2f joystickLooking = new Vector2f();

    @Override
    public void initilize(IBridge bridge)
    {
        super.initilize(bridge);

        /* Movement keybinds */
        this.movement = new KeybindCategory("movement").active(() -> bridge.get(IBridgeMenu.class).getCurrentMenu() == null);

        Keybind jump = new Keybind("jump").onPress(this::jump);
        Keybind sneak = new Keybind("sneak", this::sneak);
        Keybind back = new Keybind("back", () -> this.back = !this.back);

        this.movement.add(this.walkForward = new Keybind("walk_forward").keys(GLFW.GLFW_KEY_W));
        this.movement.add(this.walkBackward = new Keybind("walk_backward").keys(GLFW.GLFW_KEY_S));
        this.movement.add(this.walkLeft = new Keybind("walk_left").keys(GLFW.GLFW_KEY_A));
        this.movement.add(this.walkRight = new Keybind("walk_right").keys(GLFW.GLFW_KEY_D));
        this.movement.add(sneak.keys(GLFW.GLFW_KEY_LEFT_SHIFT));
        this.movement.add(jump.keys(GLFW.GLFW_KEY_SPACE));
        this.movement.add(back.keys(GLFW.GLFW_KEY_F5));
    }

    private void jump()
    {
        if (!this.canControl())
        {
            return;
        }

        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (controller != null && this.jump)
        {
            if (this.jumpGround && !controller.basic.grounded)
            {
                return;
            }

            controller.basic.velocity.y = controller.basic.sneak ? 0.4F : 0.5F;
            controller.basic.velocity.x *= 1.2F;
            controller.basic.velocity.z *= 1.2F;
            controller.basic.grounded = false;
        }
    }

    private void sneak(Boolean sneak)
    {
        if (!this.canControl())
        {
            return;
        }

        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (controller != null)
        {
            controller.basic.sneak = !sneak;
        }
    }

    @Override
    public void enable()
    {
        this.bridge.getEngine().keys.keybinds.add(this.movement);
        this.bridge.get(IBridgeCamera.class).getCameraController().add(this.camera);
    }

    @Override
    public void disable()
    {
        this.bridge.getEngine().keys.keybinds.remove(this.movement);
        this.bridge.get(IBridgeCamera.class).getCameraController().remove(this.camera);
    }

    @Override
    public void reset()
    {
        MouseInput mouse = this.bridge.getEngine().mouse;
        this.lastX = mouse.x;
        this.lastY = mouse.y;
    }

    /* ITickable */

    @Override
    public void update()
    {
        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

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

        super.update();
    }

    @Override
    protected void handleJoystick(Entity controller, JoystickInput joystick, GLFWGamepadState state)
    {
        super.handleJoystick(controller, joystick, state);

        this.joystickDirection.set(state.axes(0), state.axes(1));
        this.joystickLooking.set(state.axes(2), state.axes(3));

        if (Math.abs(this.joystickDirection.x) < 0.05F) this.joystickDirection.x = 0F;
        if (Math.abs(this.joystickDirection.y) < 0.05F) this.joystickDirection.y = 0F;
        if (Math.abs(this.joystickLooking.x) < 0.05F) this.joystickLooking.x = 0F;
        if (Math.abs(this.joystickLooking.y) < 0.05F) this.joystickLooking.y = 0F;
    }

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

    /* IMouseHandler */

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (action == GLFW.GLFW_PRESS && button == 1 && EntityUtils.isPlayer(controller))
        {
            PlayerComponent character = controller.get(PlayerComponent.class);
            ItemStack stack = character.equipment.getStack(0);

            if (!stack.isEmpty())
            {
                stack.use(controller);
            }
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {}

    /* IRenderable */

    @Override
    public void resize(int width, int height)
    {}

    @Override
    public void render(float transition)
    {
        if (this.bridge.get(IBridgeMenu.class).getCurrentMenu() != null)
        {
            return;
        }

        MouseInput mouse = this.bridge.getEngine().mouse;
        int x = mouse.x;
        int y = mouse.y;
        float sensitivity = 400F;
        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (this.firstPerson)
        {
            sensitivity *= 2;
        }

        if (controller != null && this.canControl())
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

        this.lastY = y;
        this.lastX = x;
    }

    @Override
    public void renderHUD(UIRenderingContext context, int w, int h)
    {
        if (this.firstPerson)
        {
            int x = w / 2;
            int y = h / 2;

            context.batcher.box(x - 4, y - 1, x + 3, y, Colors.A50 | Colors.WHITE);
            context.batcher.box(x - 1, y - 4, x, y + 3, Colors.A50 | Colors.WHITE);
        }

        super.renderHUD(context, w, h);
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        Entity entity = this.bridge.get(IBridgePlayer.class).getController();

        if (entity != null && this.firstPerson && this.bridge.get(IBridgeCamera.class).getCameraController().getCurrent() instanceof ThirdPersonCameraController && context.getPass() == 0)
        {
            FormComponent formComponent = entity.get(FormComponent.class);

            if (formComponent != null)
            {
                formComponent.renderFirstPerson(entity, context);
            }
        }
    }

    @Override
    public boolean canRenderEntity(Entity entity, RenderingContext context)
    {
        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (this.firstPerson && entity == controller && context.getPass() == 0 && this.bridge.get(IBridgeCamera.class).getCameraController().getCurrent() == this.camera)
        {
            return false;
        }

        return true;
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.firstPerson = data.getBool("firstPerson");
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putBool("firstPerson", this.firstPerson);
    }
}