package mchorse.bbs.game.scripts.ui;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.game.triggers.blocks.StringTriggerBlock;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.ui.ui.UIUserInterfaceMenu;

public class UserInterfaceTriggerBlock extends StringTriggerBlock
{
    @Override
    protected String getKey()
    {
        return "ui";
    }

    @Override
    public void trigger(DataContext context)
    {
        IBridge bridge = context.world.bridge;
        UserInterface ui = BBSData.getUIs().load(this.id);

        if (ui != null)
        {
            bridge.get(IBridgeMenu.class).showMenu(UIUserInterfaceMenu.create(bridge, ui));
        }
        else
        {
            bridge.get(IBridgeMenu.class).closeMenu();
        }
    }
}