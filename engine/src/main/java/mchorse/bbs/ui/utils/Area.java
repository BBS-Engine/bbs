package mchorse.bbs.ui.utils;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs.ui.utils.resizers.IResizer;
import mchorse.bbs.utils.math.MathUtils;

/**
 * Utility class for boxes
 *
 * Used in GUI for rendering and locating cursor inside of the box purposes.
 */
public class Area implements IResizer
{
    /**
     * Shared area which could be used for calculations without creating new
     * instances
     */
    public static final Area SHARED = new Area();

    /**
     * X position coordinate of the box
     */
    public int x;

    /**
     * Y position coordinate of the box
     */
    public int y;

    /**
     * Width of the box
     */
    public int w;

    /**
     * Height of the box
     */
    public int h;

    public Area()
    {}

    public Area(Area area)
    {
        this.copy(area);
    }

    public Area(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getIndex(int x, int y, int size)
    {
        return MathUtils.gridIndex(x - this.x, y - this.y, size, this.w);
    }

    public int getRows(int count, int size)
    {
        return MathUtils.gridRows(count, size, this.w);
    }

    public boolean isInside(UIContext context)
    {
        return this.isInside(context.mouseX, context.mouseY);
    }

    /**
     * Check whether given position is inside of the rect
     */
    public boolean isInside(int x, int y)
    {
        return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
    }

    /**
     * Check whether given rect intersects this rect
     */
    public boolean intersects(Area area)
    {
        return this.x < area.x + area.w && this.y < area.y + area.h
            && area.x < this.x + this.w && area.y < this.y + this.h;
    }

    /**
     * Clamp given area inside of this one
     */
    public void clamp(Area area)
    {
        int x1 = area.x;
        int y1 = area.y;
        int x2 = area.ex();
        int y2 = area.ey();

        x1 = MathUtils.clamp(x1, this.x, this.ex());
        y1 = MathUtils.clamp(y1, this.y, this.ey());
        x2 = MathUtils.clamp(x2, this.x, this.ex());
        y2 = MathUtils.clamp(y2, this.y, this.ey());

        area.setPoints(x1, y1, x2, y2);
    }

    /**
     * Expand the area either inwards or outwards
     */
    public void offset(int offset)
    {
        this.offsetX(offset);
        this.offsetY(offset);
    }

    /**
     * Expand the area either inwards or outwards (horizontally)
     */
    public void offsetX(int offset)
    {
        this.x -= offset;
        this.w += offset * 2;
    }

    /**
     * Expand the area either inwards or outwards (horizontally)
     */
    public void offsetY(int offset)
    {
        this.y -= offset;
        this.h += offset * 2;
    }

    /**
     * Set all values
     */
    public void set(int x, int y, int w, int h)
    {
        this.setPos(x, y);
        this.setSize(w, h);
    }

    /**
     * Set the position
     */
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the size
     */
    public void setSize(int w, int h)
    {
        this.w = w;
        this.h = h;
    }

    public void setPoints(int x1, int y1, int x2, int y2)
    {
        this.setPoints(x1, y1, x2, y2, 0);
    }

    public void setPoints(int x1, int y1, int x2, int y2, int offset)
    {
        int mx = Math.max(x1, x2);
        int my = Math.max(y1, y2);
        int nx = Math.min(x1, x2);
        int ny = Math.min(y1, y2);

        this.x = nx - offset;
        this.y = ny - offset;
        this.w = mx - nx + offset;
        this.h = my - ny + offset;
    }

    /**
     * Copy properties from other area 
     */
    public void copy(Area area)
    {
        this.x = area.x;
        this.y = area.y;
        this.w = area.w;
        this.h = area.h;
    }

    /**
     * Calculate X based on anchor value
     */
    public int x(float anchor)
    {
        return this.x + (int) (this.w * anchor);
    }

    /**
     * Calculate X based on anchor value with additional value
     */
    public int x(float anchor, int value)
    {
        return this.x + (int) ((this.w - value) * anchor);
    }

    /**
     * Calculate mid point X value
     */
    public int mx()
    {
        return this.x(0.5F);
    }

    /**
     * Calculate mid point X value
     */
    public int mx(int value)
    {
        return this.x(0.5F, value);
    }

    /**
     * Calculate end point X (right) value
     */
    public int ex()
    {
        return this.x + this.w;
    }

    /**
     * Calculate Y based on anchor value
     */
    public int y(float anchor)
    {
        return this.y + (int) (this.h * anchor);
    }

    /**
     * Calculate Y based on anchor value
     */
    public int y(float anchor, int value)
    {
        return this.y + (int) ((this.h - value) * anchor);
    }

    /**
     * Calculate mid point Y value
     */
    public int my()
    {
        return this.y(0.5F);
    }

    /**
     * Calculate mid point Y value
     */
    public int my(int value)
    {
        return this.y(0.5F, value);
    }

    /**
     * Calculate end point Y (bottom) value
     */
    public int ey()
    {
        return this.y + this.h;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Area)
        {
            Area area = (Area) obj;

            return this.x == area.x && this.y == area.y && this.w == area.w && this.h == area.h;
        }

        return super.equals(obj);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void render(Batcher2D batcher, int color)
    {
        this.render(batcher, color, 0, 0, 0, 0);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void render(Batcher2D batcher, int color, int offset)
    {
        this.render(batcher, color, offset, offset, offset, offset);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void render(Batcher2D batcher, int color, int horizontal, int vertical)
    {
        this.render(batcher, color, horizontal, vertical, horizontal, vertical);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void render(Batcher2D batcher, int color, int lx, int ty, int rx, int by)
    {
        batcher.box(this.x + lx, this.y + ty, this.ex() - rx, this.ey() - by, color);
    }

    /* IResizer implementation */

    @Override
    public void preApply(Area area)
    {}

    @Override
    public void apply(Area area)
    {
        area.copy(this);
    }

    @Override
    public void postApply(Area area)
    {}

    @Override
    public void add(UIElement parent, UIElement child)
    {}

    @Override
    public void remove(UIElement parent, UIElement child)
    {}

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