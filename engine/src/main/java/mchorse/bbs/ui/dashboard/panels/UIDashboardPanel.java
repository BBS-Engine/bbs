package mchorse.bbs.ui.dashboard.panels;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIDashboardPanel extends UIElement
{
    public final UIDashboard dashboard;

    public UIDashboardPanel(UIDashboard dashboard)
    {
        super();

        this.dashboard = dashboard;
        this.markContainer();
    }

    public boolean needsBackground()
    {
        return true;
    }

    public boolean canPause()
    {
        return true;
    }

    public boolean canRefresh()
    {
        return true;
    }

    public void appear()
    {}

    public void disappear()
    {}

    public void open()
    {}

    public void close()
    {}

    public void reloadWorld()
    {}

    public void update()
    {}

    public void renderInWorld(RenderingContext context)
    {}
}