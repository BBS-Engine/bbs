package mchorse.bbs.ui.dashboard.panels;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.overlay.UICRUDOverlayPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.icons.Icons;

public abstract class UICRUDDashboardPanel extends UISidebarDashboardPanel
{
    public UIIcon openOverlay;

    public final UICRUDOverlayPanel overlay;

    public UICRUDDashboardPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.overlay = this.createOverlayPanel();
        this.openOverlay = new UIIcon(Icons.MORE, (b) ->
        {
            UIOverlay.addOverlayRight(this.getContext(), this.overlay, 200, 20);
        });

        this.iconBar.prepend(this.openOverlay);

        this.keys().register(Keys.OPEN_DATA_MANAGER, this.openOverlay::clickItself);
    }

    protected abstract UICRUDOverlayPanel createOverlayPanel();

    protected abstract IKey getTitle();

    public abstract void pickData(String id);
}