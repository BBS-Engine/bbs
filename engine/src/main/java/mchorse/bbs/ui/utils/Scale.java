package mchorse.bbs.ui.utils;

import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;

/**
 * This class represents a scale of an axis 
 */
public class Scale
{
    protected double shift = 0;
    protected double zoom = 1;
    protected int mult = 1;
    public boolean inverse;

    public Area area;
    public ScrollDirection direction = ScrollDirection.HORIZONTAL;
    public float anchor;

    protected boolean lockViewport;
    protected double lockMin;
    protected double lockMax;

    public Scale(Area area, ScrollDirection direction, boolean inverse)
    {
        this(area, inverse);

        this.direction = direction;
    }

    public Scale(Area area, boolean inverse)
    {
        this(inverse);

        this.area = area;
    }

    public Scale(boolean inverse)
    {
        this.inverse = inverse;
    }

    /* Convenience methods */

    public void set(double shift, double zoom)
    {
        this.setShift(shift);
        this.setZoom(zoom);
    }

    public void anchor(float anchor)
    {
        this.anchor = anchor;
    }

    public void lock(double min, double max)
    {
        this.lockViewport = true;
        this.lockMin = Math.min(min, max);
        this.lockMax = Math.max(min, max);
    }

    public void unlock()
    {
        this.lockViewport = false;
    }

    public double getLockMin()
    {
        return this.lockMin;
    }

    public double getLockMax()
    {
        return this.lockMax;
    }

    public void calculateMultiplier()
    {
        this.mult = this.recalcMultiplier(this.zoom);
    }

    protected int recalcMultiplier(double zoom)
    {
        int factor = (int) (60F / zoom);

        /* Hardcoded caps */
        if (factor > 10000) factor = 10000;
        else if (factor > 5000) factor = 5000;
        else if (factor > 2500) factor = 2500;
        else if (factor > 1000) factor = 1000;
        else if (factor > 500) factor = 500;
        else if (factor > 250) factor = 250;
        else if (factor > 100) factor = 100;
        else if (factor > 50) factor = 50;
        else if (factor > 25) factor = 25;
        else if (factor > 10) factor = 10;
        else if (factor > 5) factor = 5;

        return factor <= 0 ? 1 : factor;
    }

    /* Getters/setters */

    public void setShift(double shift)
    {
        if (this.lockViewport)
        {
            double distance = this.getMaxValue() - this.getMinValue();

            this.shift = shift;

            double min = this.getMinValue();
            double max = this.getMaxValue();

            if (min < this.lockMin)
            {
                this.shift(this.lockMin, this.lockMin + distance);
            }
            else if (max > this.lockMax)
            {
                this.shift(this.lockMax - distance, this.lockMax);
            }

            min = this.getMinValue();
            max = this.getMaxValue();

            if (min < this.lockMin || max > this.lockMax)
            {
                double lockMin = Math.max(this.lockMin, min);
                double lockMax = Math.min(this.lockMax, max);

                this.view(lockMin, lockMax);
            }
        }
        else
        {
            this.shift = shift;
        }
    }

    public double getShift()
    {
        return this.shift;
    }

    public void setZoom(double zoom)
    {
        if (this.lockViewport)
        {
            this.zoom = zoom;

            double min = this.getMinValue();
            double max = this.getMaxValue();

            if (min < this.lockMin || max > this.lockMax)
            {
                this.view(Math.max(min, this.lockMin), Math.min(max, this.lockMax));
            }
        }
        else
        {
            this.zoom = zoom;
        }

        this.calculateMultiplier();
    }

    public double getZoom()
    {
        return this.zoom;
    }

    public int getMult()
    {
        return this.mult;
    }

    /* Graphing code */

    /**
     * Convert the value to on-screen coordinate
     */
    public double to(double value)
    {
        double factor = (!this.inverse ? value - this.shift : -value + this.shift) * this.zoom;

        if (this.area != null)
        {
            factor += this.direction.getPosition(this.area, this.anchor);
        }

        return factor;
    }

    /**
     * Convert on-screen coordinate to value
     */
    public double from(double coordinate)
    {
        if (this.area != null)
        {
            coordinate -= this.direction.getPosition(this.area, this.anchor);
        }

        return this.inverse ? -(coordinate / this.zoom - this.shift) : coordinate / this.zoom + this.shift;
    }

    public double getMinValue()
    {
        this.assertArea();

        return this.from(this.direction.getPosition(this.area, this.inverse ? 1 : 0));
    }

    public double getMaxValue()
    {
        this.assertArea();

        return this.from(this.direction.getPosition(this.area, this.inverse ? 0 : 1));
    }

    /* Viewport manipulation methods */

    public void view(double min, double max)
    {
        this.assertArea();
        this.view(min, max, this.direction.getSide(this.area));
    }

    public void view(double min, double max, double length)
    {
        this.viewOffset(min, max, length, 0);
    }

    public void viewOffset(double min, double max, double offset)
    {
        this.assertArea();
        this.viewOffset(min, max, this.direction.getSide(this.area), offset);
    }

    public void viewOffset(double min, double max, double length, double offset)
    {
        if (length <= 0)
        {
            return;
        }

        this.zoom = 1 / ((max - min) / length);

        if (offset != 0)
        {
            min -= offset / this.zoom;
            max += offset / this.zoom;
        }

        if (this.lockViewport && (min < this.lockMin || max > this.lockMax))
        {
            min = Math.max(min, this.lockMin);
            max = Math.min(max, this.lockMax);
        }

        this.zoom = 1 / ((max - min) / length);
        this.shift(min, max);

        this.calculateMultiplier();
    }

    public void shift(double min, double max)
    {
        this.shift = Interpolations.lerp(min, max, this.inverse ? 1 - this.anchor : this.anchor);
    }

    public void shiftInto(double value)
    {
        this.shiftInto(value, 0);
    }

    public void shiftInto(double value, double offset)
    {
        double min = this.getMinValue();
        double max = this.getMaxValue();
        double distance = max - min;

        if (value < min)
        {
            this.shift(value, value + distance);
        }
        else if (value > max)
        {
            value -= offset;

            this.shift(value - distance, value);
        }
    }

    public void zoom(double amount, double min, double max)
    {
        this.setZoom(MathUtils.clamp(this.zoom + amount, min, max));
    }

    public double getZoomFactor()
    {
        return this.getZoomFactor(this.zoom);
    }

    public double getZoomFactor(double zoom)
    {
        double factor = 5D;

        if (zoom < 0.2D) factor = 0.005D;
        else if (zoom < 1) factor = 0.025D;
        else if (zoom < 2) factor = 0.1D;
        else if (zoom < 15) factor = 0.5D;
        else if (zoom <= 50) factor = 1D;
        else if (zoom <= 250) factor = 2.5D;

        return factor;
    }

    protected void assertArea()
    {
        if (this.area == null)
        {
            throw new IllegalStateException("This operation isn't possible without area present!");
        }
    }
}