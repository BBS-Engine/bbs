package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.components.UIGraphicsComponent;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

/**
 * Graphic element.
 *
 * <p>This is a base interface for all graphic elements that are being constructed
 * by {@link UIGraphicsComponent}. Besides storing extra information per a type of
 * graphic, every graphic has position and relative measurements (just like
 * {@link UIComponent} has).</p>
 */
public abstract class Graphic implements IMapSerializable
{
    private static Area computed = new Area();

    public Area pixels = new Area();
    public float relativeX;
    public float relativeY;
    public float relativeW;
    public float relativeH;
    public float anchorX;
    public float anchorY;
    public int primary = Colors.WHITE;
    public boolean hover;

    /**
     * Set X in pixels relative to parent component.
     */
    public Graphic x(int value)
    {
        this.relativeX = 0F;
        this.pixels.x = value;

        return this;
    }

    /**
     * Set X relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully left, and <code>1</code> is fully right.
     */
    public Graphic rx(float value)
    {
        return this.rx(value, 0);
    }

    /**
     * Set X relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully left, and <code>1</code> is fully right.
     *
     * @param value Percentage how far into X.
     * @param offset Offset in pixels (can be negative).
     */
    public Graphic rx(float value, int offset)
    {
        this.relativeX = value;
        this.pixels.x = offset;

        return this;
    }

    /**
     * Set Y in pixels relative to parent component.
     */
    public Graphic y(int value)
    {
        this.relativeY = 0F;
        this.pixels.y = value;

        return this;
    }

    /**
     * Set Y relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully top, and <code>1</code> is fully bottom.
     */
    public Graphic ry(float value)
    {
        return this.ry(value, 0);
    }

    /**
     * Set Y relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is fully top, and <code>1</code> is fully bottom.
     *
     * @param value Percentage how far into Y.
     * @param offset Offset in pixels (can be negative).
     */
    public Graphic ry(float value, int offset)
    {
        this.relativeY = value;
        this.pixels.y = offset;

        return this;
    }

    /**
     * Set width in pixels.
     */
    public Graphic w(int value)
    {
        this.relativeW = 0F;
        this.pixels.w = value;

        return this;
    }

    /**
     * Set width relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's width, and <code>1</code> is <code>100%</code> of parent's
     * component width.
     */
    public Graphic rw(float value)
    {
        return this.rw(value, 0);
    }

    /**
     * Set width relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's width, and <code>1</code> is <code>100%</code> of parent's
     * component width.
     *
     * @param value Percentage of how wide relative to parent component.
     * @param offset Offset in pixels (can be negative).
     */
    public Graphic rw(float value, int offset)
    {
        this.relativeW = value;
        this.pixels.w = offset;

        return this;
    }

    /**
     * Set height in pixels.
     */
    public Graphic h(int value)
    {
        this.relativeH = 0F;
        this.pixels.h = value;

        return this;
    }

    /**
     * Set height relative in percents to parent component. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's height, and <code>1</code> is <code>100%</code> of parent's
     * component height.
     */
    public Graphic rh(float value)
    {
        return this.rh(value, 0);
    }

    /**
     * Set height relative in percents to parent component with offset. Passed value should be
     * <code>0..1</code>, where <code>0</code> is element will be <code>0%</code> of
     * parent component's height, and <code>1</code> is <code>100%</code> of parent's
     * component height.
     *
     * @param value Percentage of how tall relative to parent component.
     * @param offset Offset in pixels (can be negative).
     */
    public Graphic rh(float value, int offset)
    {
        this.relativeH = value;
        this.pixels.h = offset;

        return this;
    }

    /**
     * Set X and Y in pixels relative to parent component.
     */
    public Graphic xy(int x, int y)
    {
        return this.x(x).y(y);
    }

    /**
     * Set X and Y in pixels in percentage relative to parent component.
     */
    public Graphic rxy(float x, float y)
    {
        return this.rx(x).ry(y);
    }

    /**
     * Set width and height in pixels.
     */
    public Graphic wh(int w, int h)
    {
        return this.w(w).h(h);
    }

    /**
     * Set relative width and height in percentage relative to parent component.
     */
    public Graphic rwh(float w, float h)
    {
        return this.rw(w).rh(h);
    }

    /**
     * Set this graphic to display only when when mouse is over it.
     */
    public Graphic hoverOnly()
    {
        this.hover = true;

        return this;
    }

    @DiscardMethod
    public final void render(UIContext context, Area elementArea)
    {
        computed.x = elementArea.x + (int) (elementArea.w * this.relativeX) + this.pixels.x;
        computed.y = elementArea.y + (int) (elementArea.h * this.relativeY) + this.pixels.y;
        computed.w = (int) (elementArea.w * this.relativeW) + this.pixels.w;
        computed.h = (int) (elementArea.h * this.relativeH) + this.pixels.h;

        computed.x -= (int) (computed.w * this.anchorX);
        computed.y -= (int) (computed.h * this.anchorY);

        if (!this.hover || computed.isInside(context))
        {
            this.renderGraphic(context, computed);
        }
    }

    @DiscardMethod
    protected abstract void renderGraphic(UIContext context, Area area);

    @Override
    @DiscardMethod
    public void toData(MapType data)
    {
        data.putInt("x", this.pixels.x);
        data.putInt("y", this.pixels.y);
        data.putInt("w", this.pixels.w);
        data.putInt("h", this.pixels.h);
        data.putFloat("rX", this.relativeX);
        data.putFloat("rY", this.relativeY);
        data.putFloat("rW", this.relativeW);
        data.putFloat("rH", this.relativeH);
        data.putFloat("anchorX", this.anchorX);
        data.putFloat("anchorY", this.anchorY);
        data.putInt("primary", this.primary);
        data.putBool("hover", this.hover);
    }

    @Override
    @DiscardMethod
    public void fromData(MapType data)
    {
        this.pixels.x = data.getInt("x");
        this.pixels.y = data.getInt("y");
        this.pixels.w = data.getInt("w");
        this.pixels.h = data.getInt("h");
        this.relativeX = data.getFloat("rX");
        this.relativeY = data.getFloat("rY");
        this.relativeW = data.getFloat("rW");
        this.relativeH = data.getFloat("rH");
        this.anchorX = data.getFloat("anchorX");
        this.anchorY = data.getFloat("anchorY");
        this.primary = data.getInt("primary");
        this.hover = data.getBool("hover");
    }
}