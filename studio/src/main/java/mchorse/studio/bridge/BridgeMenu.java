package mchorse.studio.bridge;

import mchorse.studio.StudioEngine;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.ui.framework.UIBaseMenu;

public class BridgeMenu extends BaseBridge implements IBridgeMenu
{
    public BridgeMenu(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public UIBaseMenu getCurrentMenu()
    {
        return this.engine.screen.menu;
    }

    @Override
    public void showMenu(UIBaseMenu menu)
    {
        this.engine.screen.showMenu(menu);
    }
}