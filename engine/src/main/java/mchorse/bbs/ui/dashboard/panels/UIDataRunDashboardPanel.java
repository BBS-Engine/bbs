package mchorse.bbs.ui.dashboard.panels;

import mchorse.bbs.game.utils.manager.data.AbstractData;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.utils.Direction;

public abstract class UIDataRunDashboardPanel <T extends AbstractData> extends UIDataDashboardPanel<T>
{
    public UIIcon run;

    public UIDataRunDashboardPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.run = new UIIcon(Icons.PLAY, (b) -> this.run());
        this.run.tooltip(IKey.lang(this.getTitle().getKey() + "_run"), Direction.LEFT);
        this.iconBar.add(this.run);
    }

    protected abstract void run();

    @Override
    public void fill(T data)
    {
        super.fill(data);

        this.run.setEnabled(data != null);
    }
}