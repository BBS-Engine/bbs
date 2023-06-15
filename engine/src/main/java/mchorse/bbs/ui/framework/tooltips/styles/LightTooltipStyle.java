package mchorse.bbs.ui.framework.tooltips.styles;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class LightTooltipStyle extends TooltipStyle
{
    @Override
    public void renderBackground(UIContext context, Area area)
    {
        context.batcher.dropShadow(area.x, area.y, area.ex(), area.ey(), 4, Colors.A50, 0);
        area.render(context.batcher, Colors.WHITE);
    }

    @Override
    public int getTextColor()
    {
        return 0;
    }

    @Override
    public int getForegroundColor()
    {
        return 0;
    }
}