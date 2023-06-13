package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.objects.TriggerObject;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

public class SideScrollerGameController extends BaseGameController
{
    public KeybindCategory movement;
    public Keybind walkLeft;
    public Keybind walkRight;

    private SideScrollerCameraController camera = new SideScrollerCameraController(this);

    @Override
    public void initilize(IBridge bridge)
    {
        super.initilize(bridge);

        /* Movement keybinds */
        this.movement = new KeybindCategory("movement").active(() -> bridge.get(IBridgeMenu.class).getCurrentMenu() == null);

        Keybind jump = new Keybind("jump").onPress(this::jump);
        Keybind sneak = new Keybind("sneak", this::sneak);

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
            BasicComponent basic = controller.basic;
            float move = this.walkLeft.isDown() ? -1 : (this.walkRight.isDown() ? 1 : 0);

            if (move != 0)
            {
                float speed = 0.25F * (basic.sneak ? 0.33F : 1F) * (basic.grounded ? 1F : 0.05F) * basic.speed;

                basic.velocity.x += move * speed;
                basic.rotation.y = Interpolations.lerp(basic.rotation.y, move > 0 ? MathUtils.PI / 2 : MathUtils.PI * 1.5F, 0.5F);
            }
            else
            {
                basic.rotation.y = Interpolations.lerp(basic.rotation.y, MathUtils.PI, 0.5F);
            }
        }

        super.update();
    }

    /**
     * Pick the closest object instead of at the one looking
     */
    @Override
    protected void pickTrigger(Entity controller)
    {
        List<TriggerObject> objects = this.bridge.get(IBridgeWorld.class).getWorld().getObjects(TriggerObject.class);
        Vector3d position = new Vector3d(controller.basic.position).add(0, controller.basic.getEyeHeight(), 0);

        if (!objects.isEmpty())
        {
            objects.sort(Comparator.comparingDouble(a -> a.position.distanceSquared(position)));

            this.object = objects.get(0);

            if (this.object.position.distanceSquared(controller.basic.position) > 2 * 2)
            {
                this.object = null;
            }

            return;
        }

        this.object = null;
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
}