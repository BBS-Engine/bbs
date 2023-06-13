package mchorse.bbs.cubic.data.animation;

import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.utils.Axis;

public class AnimationVector
{
    public AnimationVector prev;
    public AnimationVector next;

    public double time;
    public AnimationInterpolation interp = AnimationInterpolation.LINEAR;
    public MolangExpression x;
    public MolangExpression y;
    public MolangExpression z;

    public double getLengthInTicks()
    {
        return this.next == null ? 0 : (this.next.time - this.time) * 20D;
    }

    public MolangExpression getStart(Axis axis)
    {
        if (axis == Axis.X)
        {
            return this.x;
        }
        else if (axis == Axis.Y)
        {
            return this.y;
        }

        return this.z;
    }

    public MolangExpression getEnd(Axis axis)
    {
        if (axis == Axis.X)
        {
            return this.next == null ? this.x : this.next.x;
        }
        else if (axis == Axis.Y)
        {
            return this.next == null ? this.y : this.next.y;
        }

        return this.next == null ? this.z : this.next.z;
    }
}