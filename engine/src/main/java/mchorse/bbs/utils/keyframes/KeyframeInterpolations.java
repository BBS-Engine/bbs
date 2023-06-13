package mchorse.bbs.utils.keyframes;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolations;

public class KeyframeInterpolations
{
    public static final IInterpolation CONSTANT = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a;
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a;
        }

        @Override
        public IKey getName()
        {
            return UIKeys.C_INTERPOLATION.get("const");
        }

        @Override
        public IKey getTooltip()
        {
            return UIKeys.C_INTERPOLATION_TIPS.get("const");
        }
    };

    public static final IInterpolation HERMITE = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public IKey getName()
        {
            return UIKeys.C_INTERPOLATION.get("hermite");
        }

        @Override
        public IKey getTooltip()
        {
            return UIKeys.C_INTERPOLATION_TIPS.get("hermite");
        }
    };

    public static final IInterpolation BEZIER = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public IKey getName()
        {
            return UIKeys.C_INTERPOLATION.get("bezier");
        }

        @Override
        public IKey getTooltip()
        {
            return UIKeys.C_INTERPOLATION_TIPS.get("bezier");
        }
    };
}