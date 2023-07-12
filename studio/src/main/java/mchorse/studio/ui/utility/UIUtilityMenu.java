package mchorse.studio.ui.utility;

import mchorse.studio.Studio;
import mchorse.studio.ui.UIKeysApp;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;

public class UIUtilityMenu extends UIBaseMenu
{
    public UIUtilityMenu(IBridge bridge)
    {
        super(bridge);

        UIOverlay.addOverlay(this.context, new UIUtilityOverlayPanel(UIKeysApp.UTILITY_TITLE, this::closeThisMenu));
    }

    @Override
    public Link getMenuId()
    {
        return Studio.link("utility");
    }

    @Override
    public boolean canPause()
    {
        return false;
    }
}