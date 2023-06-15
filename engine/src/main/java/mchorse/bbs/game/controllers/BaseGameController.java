package mchorse.bbs.game.controllers;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.core.input.JoystickInput;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.misc.hotkeys.TriggerHotkey;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.utils.keys.KeyCodes;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.objects.TriggerObject;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseGameController implements IGameController
{
    protected IBridge bridge;

    public float fov = 50;
    public Vector3f cameraOffset = new Vector3f(1, 1, 5);

    public boolean jump = true;
    public boolean jumpGround = true;

    protected TriggerObject object;

    @Override
    public boolean canControl()
    {
        Entity entity = this.bridge.get(IBridgePlayer.class).getController();
        PlayerComponent component = entity == null ? null : entity.get(PlayerComponent.class);

        return component == null || component.canControl;
    }

    @Override
    public void initilize(IBridge bridge)
    {
        this.bridge = bridge;
    }

    public IBridge getBridge()
    {
        return this.bridge;
    }

    /* Trigger object */

    @Override
    public void update()
    {
        this.checkForTriggers();

        Entity controller = this.bridge.get(IBridgePlayer.class).getController();
        JoystickInput joystick = this.bridge.getEngine().joystick;

        if (controller != null && joystick.isPresent())
        {
            this.handleJoystick(controller, joystick, joystick.getUpdatedState());
        }
    }

    protected void handleJoystick(Entity controller, JoystickInput joystick, GLFWGamepadState state)
    {
        PlayerComponent component = controller.get(PlayerComponent.class);

        if (component != null)
        {
            component.updateJoystick(state);
        }
    }

    protected void checkForTriggers()
    {
        Entity controller = this.bridge.get(IBridgePlayer.class).getController();

        if (controller != null && controller.basic.ticks % 5 == 0)
        {
            this.pickTrigger(controller);
        }
        else if (controller == null)
        {
            this.object = null;
        }
    }

    protected void pickTrigger(Entity controller)
    {
        List<TriggerObject> objects = new ArrayList<TriggerObject>();
        Vector3d position = new Vector3d(controller.basic.position).add(0, controller.basic.getEyeHeight(), 0);

        for (TriggerObject object : this.bridge.get(IBridgeWorld.class).getWorld().getObjects(TriggerObject.class))
        {
            if (object.getPickingHitbox().intersectsRay(position, controller.basic.getLook()))
            {
                objects.add(object);
            }
        }

        if (!objects.isEmpty())
        {
            objects.sort(Comparator.comparingDouble(a -> a.position.distanceSquared(position)));

            this.object = objects.get(0);

            if (this.object.position.distanceSquared(controller.basic.position) > 5 * 5)
            {
                this.object = null;
            }

            return;
        }

        this.object = null;
    }

    /* IKeyHandler */

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        Entity entity = this.bridge.get(IBridgePlayer.class).getController();

        return this.object != null && this.object.hotkeys.execute(entity, key, action == GLFW.GLFW_PRESS);
    }

    @Override
    public void handleTextInput(int key)
    {}

    @Override
    public boolean handleGamepad(int button, int action)
    {
        return false;
    }

    /* IRenderable */

    @Override
    public void renderHUD(UIRenderingContext context, int w, int h)
    {
        if (this.object == null || !this.canControl())
        {
            return;
        }

        int color = Colors.A100 | BBSSettings.primaryColor.get();
        int hx = w / 2 + 40;
        int hy = h / 2 - 40;

        FontRenderer font = context.getFont();

        for (TriggerHotkey hotkey : this.object.hotkeys.hotkeys)
        {
            String combo = KeyCodes.getName(hotkey.keycode);
            int kw = font.getWidth(combo);

            context.batcher.box(hx - 2, hy - 2, hx + kw + 2, hy + font.getHeight() + 2, color);
            context.batcher.text(combo, hx, hy);
            context.batcher.textShadow(hotkey.title, hx + kw + 5, hy);

            hy += 24;
        }
    }

    /* IMapSerializable */

    @Override
    public void fromData(MapType data)
    {
        this.fov = data.getFloat("fov", 50);
        this.jump = data.getBool("jump", this.jump);
        this.jumpGround = data.getBool("jumpGround", this.jumpGround);
        this.cameraOffset.set(DataStorageUtils.vector3fFromData(data.getList("cameraOffset"), this.cameraOffset));
    }

    @Override
    public void toData(MapType data)
    {
        data.putFloat("fov", this.fov);
        data.putBool("jump", this.jump);
        data.putBool("jumpGround", this.jumpGround);
        data.put("cameraOffset", DataStorageUtils.vector3fToData(this.cameraOffset));
    }
}