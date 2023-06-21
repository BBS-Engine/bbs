package mchorse.bbs.ui.game.utils.overlays;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Collection;
import java.util.function.Consumer;

public class UIContentNamesOverlayPanel extends UIStringOverlayPanel
{
    public UIIcon edit;

    private ContentType type;

    public UIContentNamesOverlayPanel(IKey title, ContentType type, Collection<String> strings, Consumer<String> callback)
    {
        super(title, strings, callback);

        this.type = type;
        this.edit = new UIIcon(Icons.EDIT, (b) -> this.edit(this.getValue()));

        this.icons.add(this.edit);
    }

    private void edit(String text)
    {
        this.openPanel(text, this.type);
        this.close();
    }

    private void openPanel(String text, ContentType type)
    {
        if (text.isEmpty())
        {
            return;
        }

        IBridge bridge = this.getContext().menu.bridge;
        UIBaseMenu currentMenu = bridge.get(IBridgeMenu.class).getCurrentMenu();

        if (currentMenu instanceof UIDashboard)
        {
            UIDashboard dashboard = (UIDashboard) currentMenu;
            UIDataDashboardPanel panel = type.get(dashboard);

            dashboard.setPanel(panel);
            panel.pickData(text);
            panel.overlay.namesList.setCurrentFile(text);
        }
    }
}