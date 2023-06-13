package mchorse.bbs.utils;

import mchorse.bbs.utils.math.MathUtils;

public class Range
{
    public boolean enabled;
    public int min;
    public int max;

    public static Range fromString(String string)
    {
        try
        {
            String[] splits = string.split(",");
            int min = Integer.parseInt(splits[0]);
            int max = min;

            if (splits.length >= 2)
            {
                max = Integer.parseInt(splits[1]);
            }

            Range range = new Range(min, max);

            range.enabled = true;

            return range;
        }
        catch (Exception e)
        {}

        return null;
    }

    public Range(int min, int max)
    {
        this.set(min, max);
    }

    public void set(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public void normalize()
    {
        int min = this.min;
        int max = this.max;

        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    public int calculateOffset()
    {
        this.normalize();

        return this.max - this.min;
    }

    public double apply(double x)
    {
        return this.enabled ? MathUtils.clamp(x, this.min, this.max) : x;
    }

    public String stringify()
    {
        return this.min + "," + this.max;
    }
}