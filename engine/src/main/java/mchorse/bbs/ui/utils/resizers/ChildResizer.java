package mchorse.bbs.ui.utils.resizers;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Area;

public class ChildResizer extends DecoratedResizer
{
    public IParentResizer parent;
    public UIElement element;
    private int x;
    private int y;
    private int w;
    private int h;

    public ChildResizer(IParentResizer parent, UIElement element)
    {
        super(element.getFlex());

        this.parent = parent;
        this.element = element;
    }

    @Override
    public void apply(Area area)
    {
        if (this.resizer != null)
        {
            this.resizer.apply(area);
        }

        this.parent.apply(area, this.resizer, this);
        this.x = area.x;
        this.y = area.y;
        this.w = area.w;
        this.h = area.h;
    }

    @Override
    public void postApply(Area area)
    {
        if (this.resizer != null)
        {
            this.resizer.postApply(area);
        }
    }

    @Override
    public void add(UIElement parent, UIElement child)
    {
        if (this.resizer != null)
        {
            this.resizer.add(parent, child);
        }
    }

    @Override
    public void remove(UIElement parent, UIElement child)
    {
        if (this.resizer != null)
        {
            this.resizer.remove(parent, child);
        }
    }

    @Override
    public int getX()
    {
        return this.x;
    }

    @Override
    public int getY()
    {
        return this.y;
    }

    @Override
    public int getW()
    {
        return this.w;
    }

    @Override
    public int getH()
    {
        return this.h;
    }
}
