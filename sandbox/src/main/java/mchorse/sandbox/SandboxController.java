package mchorse.sandbox;

import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.world.entities.Entity;
import org.lwjgl.glfw.GLFW;

public class SandboxController implements ITickable, IMouseHandler, IKeyHandler
{
    public SandboxEngine engine;

    public Entity controller;
    public Entity player;
    public boolean creative;

    public SandboxController(SandboxEngine engine)
    {
        this.engine = engine;

        /* General keybinds */
        KeybindCategory general = new KeybindCategory("general").active(() -> engine.get(IBridgeMenu.class).getCurrentMenu() == null);

        Keybind pause = new Keybind("pause").onPress(this::pause);

        general.add(pause.keys(GLFW.GLFW_KEY_ESCAPE));

        engine.keys.keybinds.add(general);
    }

    /* Callbacks */

    private void pause()
    {
        this.engine.screen.pause();
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
    {}

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
}