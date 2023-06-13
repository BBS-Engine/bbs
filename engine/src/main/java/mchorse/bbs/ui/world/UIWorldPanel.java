package mchorse.bbs.ui.world;

import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Colors;

public class UIWorldPanel extends UIDashboardPanel implements IFlightSupported
{
    public UIWorldPanel(UIDashboard dashboard)
    {
        super(dashboard);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public void render(UIContext context)
    {
        context.draw.gradientHBox(this.area.x, this.area.y, this.area.x + 50, this.area.ey(), Colors.A50, 0);
        context.draw.gradientHBox(this.area.ex() - 50, this.area.y, this.area.ex(), this.area.ey(), 0, Colors.A50);

        super.render(context);
    }
}