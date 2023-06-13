package mchorse.bbs.bridge;

import mchorse.bbs.ui.framework.UIBaseMenu;

public interface IBridgeMenu
{
    public UIBaseMenu getCurrentMenu();

    public default void closeMenu()
    {
        this.showMenu(null);
    }

    public void showMenu(UIBaseMenu menu);
}