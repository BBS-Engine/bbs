package mchorse.bbs.ui.utils.resizers.layout;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.resizers.AutomaticResizer;
import mchorse.bbs.ui.utils.resizers.ChildResizer;
import mchorse.bbs.ui.utils.resizers.IResizer;

public class GridResizer extends AutomaticResizer
{
    private int i;
    private int x;
    private int y;
    private int h;

    /**
     * How many elements in a row
     */
    private int items = 2;

    /**
     * If above zero, what is the width of every cell should be,
     * instead of items per row
     */
    private int width = 0;

    /**
     * Whether this resizes changes the bounds of the area
     */
    private boolean resizes = true;

    public static GridResizer apply(UIElement element, int margin)
    {
        GridResizer resizer = new GridResizer(element, margin);

        element.post(resizer);

        return resizer;
    }

    protected GridResizer(UIElement parent, int margin)
    {
        super(parent, margin);
    }

    public GridResizer resizes(boolean resizes)
    {
        this.resizes = resizes;

        return this;
    }

    public GridResizer items(int items)
    {
        this.items = items;

        return this;
    }

    /**
     * If you use this feature, make sure to resize the elements twice, as
     * the it needs parent's width, and it's not available on the first
     * layout resizing pass
     *
     * TODO: maybe fix it?
     */
    public GridResizer width(int width)
    {
        this.width = width;

        return this;
    }

    @Override
    public void apply(Area area)
    {
        this.i = this.x = this.y = this.h = 0;
    }

    @Override
    public void apply(Area area, IResizer resizer, ChildResizer child)
    {
        int w;
        int h;
        int x;
        int y;

        if (this.width > 0)
        {
            if (this.x + this.width > this.parent.area.w - this.padding * 2)
            {
                this.y += this.h + this.margin;
                this.h = 0;
                this.x = 0;
            }

            w = this.width;
            h = resizer == null ? 0 : resizer.getH();
            x = this.parent.area.x + this.padding + this.x;
            y = this.parent.area.y + this.padding + this.y;

            if (h <= 0)
            {
                h = this.height;
            }

            if (h <= 0)
            {
                h = w;
            }

            this.h = Math.max(this.h, h);

            area.set(x, y, w, h);

            this.x += this.width + this.margin;
        }
        else
        {
            if (this.i != 0 && this.i % this.items == 0)
            {
                this.y += this.h + this.margin;
                this.h = 0;
                this.i = 0;
            }

            w = (this.parent.area.w - this.padding * 2 - this.margin * (this.items - 1)) / this.items;
            h = resizer == null ? 0 : resizer.getH();
            x = this.parent.area.x + this.padding + (w + this.margin) * this.i;
            y = this.parent.area.y + this.padding + this.y;

            if (h <= 0)
            {
                h = this.height;
            }

            if (h <= 0)
            {
                h = w;
            }

            this.h = Math.max(this.h, h);
            this.i ++;

            area.set(x, y, w, h);
        }
    }

    @Override
    public int getH()
    {
        if (this.resizes)
        {
            int i = 0;
            int x = 0;
            int y = 0;
            int maxH = 0;
            int width = this.parent.area.w;

            if (this.width > 0)
            {
                for (ChildResizer child : this.getResizers())
                {
                    if (x + this.width > width - this.padding * 2)
                    {
                        y += maxH + this.margin;
                        maxH = 0;
                        x = 0;
                    }

                    int w = this.width;
                    int h = child.resizer == null ? 0 : child.resizer.getH();

                    if (h <= 0)
                    {
                        h = this.height;
                    }

                    if (h <= 0)
                    {
                        h = w;
                    }

                    maxH = Math.max(maxH, h);
                    x += this.width + this.margin;
                }
            }
            else
            {
                for (ChildResizer child : this.getResizers())
                {
                    if (i != 0 && i % this.items == 0)
                    {
                        y += maxH + this.margin;
                        maxH = 0;
                        i = 0;
                    }

                    int w = (width - this.padding * 2 - this.margin * (this.items - 1)) / this.items;
                    int h = child.resizer == null ? 0 : child.resizer.getH();

                    if (h <= 0)
                    {
                        h = this.height;
                    }

                    if (h <= 0)
                    {
                        h = w;
                    }

                    maxH = Math.max(maxH, h);

                    i++;
                }
            }

            return y + maxH + this.padding * 2;
        }

        return super.getH();
    }
}