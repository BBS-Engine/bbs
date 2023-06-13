package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TopDownGameController extends BaseGameController
{
    public KeybindCategory movement;
    public Keybind walkForward;
    public Keybind walkBackward;
    public Keybind walkLeft;
    public Keybind walkRight;

    public float pitch;

    private TopDownCameraController camera = new TopDownCameraController(this);

    private final Vector3f direction = new Vector3f();

    @Override
    public void initilize(IBridge bridge)
    {
        super.initilize(bridge);

        /* Movement keybinds */
        this.movement = new KeybindCategory("movement").active(() -> bridge.get(IBridgeMenu.class).getCurrentMenu() == null);

        Keybind jump = new Keybind("jump").onPress(this::jump);
        Keybind sneak = new Keybind("sneak", this::sneak);

        this.movement.add(this.walkForward = new Keybind("walk_forward").keys(GLFW.GLFW_KEY_W));
        this.movement.add(this.walkBackward = new Keybind("walk_backward").keys(GLFW.GLFW_KEY_S));
        this.movement.add(this.walkLeft = new Keybind("walk_left").keys(GLFW.GLFW_KEY_A));
        this.movement.add(this.walkRight = new Keybind("walk_right").keys(GLFW.GLFW_KEY_D));
        this.movement.add(sneak.keys(GLFW.GLFW_KEY_LEFT_SHIFT));
        this.movement.add(jump.keys(GLFW.GLFW_KEY_SPACE));
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
    {}

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

            if (moveZ != 0 || moveX != 0)
            {
                this.direction.set(moveX, 0, moveZ).normalize().mul(0.25F);

                BasicComponent basic = controller.basic;

                this.direction.mul((basic.sneak ? 0.33F : 1F) * (basic.grounded ? 1F : 0.05F) * basic.speed);

                basic.velocity.x += this.direction.x;
                basic.velocity.z += this.direction.z;

                float angle = -new Vector2f(moveX, moveZ).angle(new Vector2f(0, -1));
                float dA = basic.rotation.y - angle;

                /* Fix 360+ rotations */
                if (Math.abs(dA) > MathUtils.PI)
                {
                    float a = Math.copySign(MathUtils.PI * 2, -dA);

                    basic.rotation.y += a;
                    basic.prevRotation.y += a;

                    basic.rotation.z += a;
                    basic.prevRotation.z += a;
                }

                basic.rotation.y = Interpolations.lerp(basic.rotation.y, angle, 0.5F);
            }
        }

        super.update();
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
    {}

    @Override
    public void renderInWorld(RenderingContext context)
    {}

    @Override
    public boolean canRenderEntity(Entity entity, RenderingContext context)
    {
        return true;
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.pitch = data.getFloat("pitch");
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putFloat("pitch", this.pitch);
    }
}