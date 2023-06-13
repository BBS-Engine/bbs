package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.code.ui.ScriptUIBuilder;
import mchorse.bbs.game.scripts.code.ui.ScriptUIContext;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.user.global.IScriptUI;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.game.scripts.user.ui.IScriptUIContext;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.ui.UIUserInterfaceMenu;

public class ScriptUI implements IScriptUI
{
    private ScriptBBS factory;

    public ScriptUI(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public IScriptUIBuilder createFromData(String id, String script, String function)
    {
        UserInterface ui = BBSData.getUIs().load(id);

        if (ui != null)
        {
            script = script == null ? "" : script;
            function = function == null ? "" : function;

            return new ScriptUIBuilder(ui, script, function);
        }

        return null;
    }

    @Override
    public IScriptUIBuilder create(String script, String function)
    {
        script = script == null ? "" : script;
        function = function == null ? "" : function;

        return new ScriptUIBuilder(new UserInterface(), script, function);
    }

    @Override
    public boolean open(IScriptUIBuilder in, boolean defaultData)
    {
        if (!(in instanceof ScriptUIBuilder))
        {
            return false;
        }

        ScriptUIBuilder builder = (ScriptUIBuilder) in;

        if (this.getUIContext() == null)
        {
            IBridge bridge = this.factory.getBridge();
            Object object = bridge.get(IBridgePlayer.class).getController();

            if (object == null)
            {
                object = bridge.get(IBridgeWorld.class).getWorld();
            }

            UserInterface ui = builder.getUI();
            UserInterfaceContext context = new UserInterfaceContext(ui, object, builder.getScript(), builder.getFunction());

            bridge.get(IBridgeMenu.class).showMenu(new UIUserInterfaceMenu(bridge, context));

            if (defaultData)
            {
                context.populateDefaultData();
            }

            context.clearChanges();

            return true;
        }

        return false;
    }

    @Override
    public void close()
    {
        this.factory.getBridge().get(IBridgeMenu.class).closeMenu();
    }

    @Override
    public IScriptUIContext getUIContext()
    {
        UIBaseMenu menu = this.factory.getBridge().get(IBridgeMenu.class).getCurrentMenu();

        if (menu instanceof UIUserInterfaceMenu)
        {
            return new ScriptUIContext(((UIUserInterfaceMenu) menu).uiContext);
        }

        return null;
    }
}