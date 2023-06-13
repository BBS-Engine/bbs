package mchorse.app;

import mchorse.app.ui.player.UIBasicPlayerMenu;
import mchorse.app.ui.player.UICustomPlayerMenu;
import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.world.entities.Entity;
import org.lwjgl.glfw.GLFW;

public class GameController implements ITickable, IMouseHandler, IKeyHandler
{
    public GameEngine engine;

    public Entity controller;
    public Entity player;
    public boolean creative;

    public GameController(GameEngine engine)
    {
        this.engine = engine;

        /* General keybinds */
        KeybindCategory general = new KeybindCategory("general").active(() -> engine.get(IBridgeMenu.class).getCurrentMenu() == null);

        Keybind pause = new Keybind("pause").onPress(this::pause);
        Keybind inventory = new Keybind("inventory", this::openInventory);

        general.add(pause.keys(GLFW.GLFW_KEY_ESCAPE));
        general.add(inventory.keys(GLFW.GLFW_KEY_I));

        engine.keys.keybinds.add(general);
    }

    /* Callbacks */

    private void pause()
    {
        this.engine.screen.pause();
    }

    private void openInventory()
    {
        if (!this.canControl())
        {
            return;
        }

        Entity controller = this.getController();

        if (EntityUtils.isPlayer(controller))
        {
            if (!this.tryOpenCustomInventory())
            {
                this.engine.screen.showMenu(new UIBasicPlayerMenu(this.engine));
            }
        }
    }

    private boolean tryOpenCustomInventory()
    {
        String inventoryUI = BBSData.getSettings().inventoryUI.get();
        UserInterface ui = inventoryUI.isEmpty() ? null : BBSData.getUIs().load(inventoryUI);

        if (ui != null)
        {
            try
            {
                this.engine.screen.showMenu(new UICustomPlayerMenu(this.engine, UserInterfaceContext.create(ui, this.getController())));

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    /* Getters/setters */

    public boolean canControl()
    {
        return this.engine.playerData.getGameController().canControl();
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
        else if (this.player == null)
        {
            this.setupPlayer();
        }
    }

    public void init()
    {
        this.reset();
    }

    private void setupPlayer()
    {
        this.player = this.engine.playerData.createPlayer(this.engine.world.architect);

        this.engine.world.addEntity(this.player);
    }

    public void reload()
    {
        this.creative = this.engine.development;
        this.player = null;

        if (!this.engine.development)
        {
            this.setupPlayer();
        }
    }

    public void reset()
    {
        this.engine.playerData.getGameController().reset();
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

        this.engine.playerData.getGameController().update();
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

        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE)
        {
            Trigger trigger = BBSData.getSettings().playerMouseClick;

            if (!trigger.isEmpty())
            {
                DataContext data = new DataContext(controller)
                    .set("button", button)
                    .set("down", action == GLFW.GLFW_PRESS);

                trigger.trigger(data);

                if (data.isCanceled())
                {
                    return;
                }
            }
        }

        this.engine.playerData.getGameController().handleMouse(button, action, mode);
    }

    @Override
    public void handleScroll(double x, double y)
    {
    }

    /* IKeyHandler */

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        Entity controller = this.getController();

        if (controller == null || this.engine.screen.hasMenu() || !this.canControl())
        {
            return false;
        }

        if (this.engine.playerData.getGameController().handleKey(key, scancode, action, mods))
        {
            return true;
        }

        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE)
        {
            return BBSData.getSettings().hotkeys.execute(controller, key, action == GLFW.GLFW_PRESS);
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {}
}