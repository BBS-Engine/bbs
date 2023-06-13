package mchorse.bbs.ui.utils.resizers.layout;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.resizers.AutomaticResizer;
import mchorse.bbs.ui.utils.resizers.ChildResizer;
import mchorse.bbs.ui.utils.resizers.IResizer;

import java.util.List;

public class RowResizer extends AutomaticResizer
{
    private int i;
    private int x;
    private int w;
    private int count;

    /**
     * Preferred element to use in the row for the width adjustments caused by
     * integer arithmetics, -1 = to the size() / 2
     */
    private int preferred = -1;

    /**
     * Default width for row elements if not specified by resizer
     */
    private int width;

    /**
     * Whether the area should be resized according to the sum or row elements
     */
    private boolean resize;

    /**
     * Whether the elements would be placed from right to left
     */
    private boolean reverse;

    public static RowResizer apply(UIElement element, int margin)
    {
        RowResizer resizer = new RowResizer(element, margin);

        element.post(resizer);

        return resizer;
    }

    protected RowResizer(UIElement parent, int margin)
    {
        super(parent, margin);
    }

    public RowResizer preferred(int index)
    {
        this.preferred = i;

        return this;
    }

    public RowResizer width(int width)
    {
        this.width = width;

        return this;
    }

    public RowResizer resize()
    {
        this.resize = true;

        return this;
    }

    public RowResizer reverse()
    {
        this.reverse = true;

        return this;
    }

    @Override
    public void apply(Area area)
    {
        List<ChildResizer> resizers = this.getResizers();

        this.i = this.x = this.w = 0;
        this.count = resizers.size();

        for (ChildResizer resizer : resizers)
        {
            int w = Math.max(resizer.resizer == null ? 0 : resizer.resizer.getW(), 0);

            if (w > 0)
            {
                this.w += w;
                this.count --;
            }
        }
    }

    @Override
    public void apply(Area area, IResizer resizer, ChildResizer child)
    {
        List<ChildResizer> resizers = this.getResizers();
        int c = resizers.size();
        int original = this.parent.area.w - this.padding * 2 - this.margin * (c - 1);
        int w = this.count > 0 ? (original - this.w) / this.count : 0;
        int x = this.parent.area.x + this.padding + this.x + child.element.margin.left;

        /* If it's reverse, start adding from the right side */
        if (this.reverse)
        {
            x = this.parent.area.ex() - this.padding - this.x - child.element.margin.right;
        }

        /* If resizer specifies its custom width, use that one instead */
        int cw = resizer == null ? 0 : resizer.getW();
        int ch = resizer == null ? this.height : resizer.getH();

        if (this.width > 0)
        {
            cw = this.width;
        }

        cw = cw > 0 ? cw : w;

        /* Readjust the middle element width to balance out int imprecision */
        int preferred = this.preferred == -1 ? c / 2 : this.preferred;

        if (this.i == preferred && !this.resize && this.width <= 0)
        {
            int diff = original - this.w - w * this.count;

            if (diff > 0)
            {
                cw += diff;
            }
        }

        /* Subtract the width from the X position */
        if (this.reverse)
        {
            x -= cw;
        }

        area.set(x, this.parent.area.y + this.padding + child.element.margin.top, cw, ch > 0 ? ch : this.parent.area.h - this.padding * 2);

        this.x += cw + this.margin + child.element.margin.horizontal();
        this.i ++;
    }

    @Override
    public int getW()
    {
        if (this.resize)
        {
            List<ChildResizer> resizers = this.getResizers();
            int w = resizers.isEmpty() ? 0 : -this.margin;

            for (ChildResizer resizer : resizers)
            {
                int cw = resizer.resizer == null ? 0 : resizer.resizer.getW();

                if (cw == 0 && this.width > 0)
                {
                    cw = this.width;
                }

                w += Math.max(cw, 0) + this.margin + resizer.element.margin.horizontal();
            }

            return w + this.padding * 2;
        }

        return 0;
    }

    @Override
    public int getH()
    {
        List<ChildResizer> resizers = this.getResizers();
        int h = 0;

        for (ChildResizer child : resizers)
        {
            h = Math.max(h, child.resizer == null ? 0 : child.resizer.getH() + child.element.margin.vertical());
        }

        if (h == 0)
        {
            h = this.height;
        }

        return h + this.padding * 2;
    }
}