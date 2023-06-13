package mchorse.bbs.ui.framework.tooltips.styles;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public abstract class TooltipStyle
{
    public static final TooltipStyle LIGHT = new LightTooltipStyle();
    public static final TooltipStyle DARK = new DarkTooltipStyle();

    public static TooltipStyle get()
    {
        return get(BBSSettings.tooltipStyle.get());
    }

    public static TooltipStyle get(int style)
    {
        if (style == 0)
        {
            return LIGHT;
        }

        return DARK;
    }

    public abstract void renderBackground(UIContext context, Area area);

    public abstract int getTextColor();

    public abstract int getForegroundColor();
}