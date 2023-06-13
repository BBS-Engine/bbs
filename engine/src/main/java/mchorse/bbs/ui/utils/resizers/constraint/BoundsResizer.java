package mchorse.bbs.ui.utils.resizers.constraint;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.UIViewportStack;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.resizers.DecoratedResizer;
import mchorse.bbs.ui.utils.resizers.IResizer;
import mchorse.bbs.utils.math.MathUtils;

/**
 * Bounds resizer
 *
 * This resizer class allows to keep the element within the bounds of
 * current viewport
 */
public class BoundsResizer extends DecoratedResizer
{
    public UIElement target;
    public int padding;

    private UIViewportStack viewport = new UIViewportStack();

    public static BoundsResizer apply(UIElement element, UIElement target, int padding)
    {
        BoundsResizer resizer = new BoundsResizer(element.resizer(), target, padding);

        element.post(resizer);

        return resizer;
    }

    protected BoundsResizer(IResizer resizer, UIElement target, int padding)
    {
        super(resizer);

        this.target = target;
        this.padding = padding;
    }

    @Override
    public void apply(Area area)
    {
        this.viewport.applyFromElement(this.target);

        Area viewport = this.viewport.getViewport();

        area.x = MathUtils.clamp(area.x, this.viewport.globalX(viewport.x) + this.padding, this.viewport.globalX(viewport.ex()) - area.w - this.padding);
        area.y = MathUtils.clamp(area.y, this.viewport.globalY(viewport.y) + this.padding, this.viewport.globalY(viewport.ey()) - area.h - this.padding);

        this.viewport.reset();
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public int getW()
    {
        return 0;
    }

    @Override
    public int getH()
    {
        return 0;
    }
}