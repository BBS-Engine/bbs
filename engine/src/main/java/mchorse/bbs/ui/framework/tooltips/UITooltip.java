package mchorse.bbs.ui.framework.tooltips;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public class UITooltip
{
    public UIElement element;
    public Area area = new Area();

    public void set(UIContext context, UIElement element)
    {
        this.element = element;

        if (element != null)
        {
            this.area.copy(element.area);
            this.area.x = context.globalX(this.area.x);
            this.area.y = context.globalY(this.area.y);
        }
    }

    public void render(ITooltip tooltip, UIContext context)
    {
        if (this.element == null || tooltip == null)
        {
            return;
        }

        tooltip.renderTooltip(context);
    }

    public void render(UIContext context)
    {
        if (this.element != null)
        {
            this.element.renderTooltip(context, this.area);
        }
    }
}