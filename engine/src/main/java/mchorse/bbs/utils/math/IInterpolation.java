package mchorse.bbs.utils.math;

import mchorse.bbs.l10n.keys.IKey;

public interface IInterpolation
{
    public float interpolate(float a, float b, float x);

    public double interpolate(double a, double b, double x);

    public IKey getName();

    public IKey getTooltip();
}
