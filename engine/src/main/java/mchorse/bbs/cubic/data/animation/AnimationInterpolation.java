package mchorse.bbs.cubic.data.animation;

import mchorse.bbs.cubic.MolangHelper;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.keyframes.KeyframeInterpolations;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;

import java.util.Objects;

public enum AnimationInterpolation
{
    LINEAR("linear", Interpolation.LINEAR), HERMITE("catmullrom", null)
    {
        @Override
        public double interpolate(AnimationVector vector, MolangHelper.Component component, Axis axis, double factor)
        {
            double start = MolangHelper.getValue(vector.getStart(axis), component, axis);
            double destination = MolangHelper.getValue(vector.getEnd(axis), component, axis);

            double pre = start;
            double post = destination;

            if (vector.prev != null)
            {
                pre = MolangHelper.getValue(vector.prev.getStart(axis), component, axis);
            }

            if (vector.next != null)
            {
                post = MolangHelper.getValue(vector.next.getEnd(axis), component, axis);
            }

            return Interpolations.cubicHermite(pre, start, destination, post, factor);
        }
    },
    STEP("step", KeyframeInterpolations.CONSTANT),
    SINE_IN("easeInSine", Interpolation.SINE_IN), SINE_OUT("easeOutSine", Interpolation.SINE_OUT), SINE_INOUT("easeInOutSine", Interpolation.SINE_INOUT),
    QUAD_IN("easeInQuad", Interpolation.QUAD_IN), QUAD_OUT("easeOutQuad", Interpolation.QUAD_OUT), QUAD_INOUT("easeInOutQuad", Interpolation.QUAD_INOUT),
    CUBIC_IN("easeInCubic", Interpolation.CUBIC_IN), CUBIC_OUT("easeOutCubic", Interpolation.CUBIC_OUT), CUBIC_INOUT("easeInOutCubic", Interpolation.CUBIC_INOUT),
    QUART_IN("easeInQuart", Interpolation.QUART_IN), QUART_OUT("easeOutQuart", Interpolation.QUART_OUT), QUART_INOUT("easeInOutQuart", Interpolation.QUART_INOUT),
    QUINT_IN("easeInQuint", Interpolation.QUINT_IN), QUINT_OUT("easeOutQuint", Interpolation.QUINT_OUT), QUINT_INOUT("easeInOutQuint", Interpolation.QUINT_INOUT),
    EXP_IN("easeInExpo", Interpolation.EXP_IN), EXP_OUT("easeOutExpo", Interpolation.EXP_OUT), EXP_INOUT("easeInOutExpo", Interpolation.EXP_INOUT),
    CIRCLE_IN("easeInCirc", Interpolation.CIRCLE_IN), CIRCLE_OUT("easeOutCirc", Interpolation.CIRCLE_OUT), CIRCLE_INOUT("easeInOutCirc", Interpolation.CIRCLE_INOUT),
    BACK_IN("easeInBack", Interpolation.BACK_IN), BACK_OUT("easeOutBack", Interpolation.BACK_OUT), BACK_INOUT("easeInOutBack", Interpolation.BACK_INOUT),
    /* These are inverted (i.e. in and out swapped places) because that's how the GeckoLib plugin shows */
    ELASTIC_IN("easeInElastic", Interpolation.ELASTIC_OUT), ELASTIC_OUT("easeOutElastic", Interpolation.ELASTIC_IN), ELASTIC_INOUT("easeInOutElastic", Interpolation.ELASTIC_INOUT),
    BOUNCE_IN("easeInBounce", Interpolation.BOUNCE_OUT), BOUNCE_OUT("easeOutBounce", Interpolation.BOUNCE_IN), BOUNCE_INOUT("easeInOutBounce", Interpolation.BOUNCE_INOUT);

    public final String name;
    public final IInterpolation interp;

    public static AnimationInterpolation byName(String easing)
    {
        for (AnimationInterpolation interp : values())
        {
            if (Objects.equals(interp.name, easing))
            {
                return interp;
            }
        }

        return LINEAR;
    }

    private AnimationInterpolation(String name, IInterpolation interp)
    {
        this.name = name;
        this.interp = interp;
    }

    private AnimationInterpolation()
    {
        this.name = null;
        this.interp = null;
    }

    public double interpolate(AnimationVector vector, MolangHelper.Component component, Axis axis, double factor)
    {
        if (vector.next != null && vector.next.interp != null && vector.next.interp.interp != null)
        {
            factor = vector.next.interp.interp.interpolate(0, 1, factor);
        }

        double start = MolangHelper.getValue(vector.getStart(axis), component, axis);
        double destination = MolangHelper.getValue(vector.getEnd(axis), component, axis);

        return Interpolations.lerp(start, destination, factor);
    }
}