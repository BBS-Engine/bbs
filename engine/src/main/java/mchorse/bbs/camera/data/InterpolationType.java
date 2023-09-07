package mchorse.bbs.camera.data;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.context.ContextAction;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;
import mchorse.bbs.utils.math.Interpolation;
import org.lwjgl.glfw.GLFW;

public enum InterpolationType
{
    LINEAR(Interpolation.LINEAR, KeyframeInterpolation.LINEAR), CUBIC("cubic", KeyframeInterpolation.CUBIC, GLFW.GLFW_KEY_X), HERMITE("hermite", KeyframeInterpolation.HERMITE, GLFW.GLFW_KEY_H),
    /* Quadratic interpolations */
    QUAD_IN(Interpolation.QUAD_IN, KeyframeInterpolation.QUAD, KeyframeEasing.IN), QUAD_OUT(Interpolation.QUAD_OUT, KeyframeInterpolation.QUAD, KeyframeEasing.OUT), QUAD_INOUT(Interpolation.QUAD_INOUT, KeyframeInterpolation.QUAD, KeyframeEasing.INOUT),
    /* Cubic interpolations */
    CUBIC_IN(Interpolation.CUBIC_IN, KeyframeInterpolation.CUBIC, KeyframeEasing.IN), CUBIC_OUT(Interpolation.CUBIC_OUT, KeyframeInterpolation.CUBIC, KeyframeEasing.OUT), CUBIC_INOUT(Interpolation.CUBIC_INOUT, KeyframeInterpolation.CUBIC, KeyframeEasing.INOUT),
    /* Exponential interpolations */
    EXP_IN(Interpolation.EXP_IN, KeyframeInterpolation.EXP, KeyframeEasing.IN), EXP_OUT(Interpolation.EXP_OUT, KeyframeInterpolation.EXP, KeyframeEasing.OUT), EXP_INOUT(Interpolation.EXP_INOUT, KeyframeInterpolation.EXP, KeyframeEasing.INOUT),
    /* Back interpolations */
    BACK_IN(Interpolation.BACK_IN, KeyframeInterpolation.BACK, KeyframeEasing.IN), BACK_OUT(Interpolation.BACK_OUT, KeyframeInterpolation.BACK, KeyframeEasing.OUT), BACK_INOUT(Interpolation.BACK_INOUT, KeyframeInterpolation.BACK, KeyframeEasing.INOUT),
    /* Back interpolations */
    ELASTIC_IN(Interpolation.ELASTIC_IN, KeyframeInterpolation.ELASTIC, KeyframeEasing.IN), ELASTIC_OUT(Interpolation.ELASTIC_OUT, KeyframeInterpolation.ELASTIC, KeyframeEasing.OUT), ELASTIC_INOUT(Interpolation.ELASTIC_INOUT, KeyframeInterpolation.ELASTIC, KeyframeEasing.INOUT),
    /* Back interpolations */
    BOUNCE_IN(Interpolation.BOUNCE_IN, KeyframeInterpolation.BOUNCE, KeyframeEasing.IN), BOUNCE_OUT(Interpolation.BOUNCE_OUT, KeyframeInterpolation.BOUNCE, KeyframeEasing.OUT), BOUNCE_INOUT(Interpolation.BOUNCE_INOUT, KeyframeInterpolation.BOUNCE, KeyframeEasing.INOUT),
    /* Exclusive (no way to convert properly to keyframe) */
    CIRCULAR("circle", KeyframeInterpolation.LINEAR, GLFW.GLFW_KEY_A);

    public final String name;
    public Interpolation function;
    public KeyframeInterpolation interp;
    public KeyframeEasing easing = KeyframeEasing.IN;
    private int keybind;

    public static InterpolationType fromInterp(Interpolation interp)
    {
        for (InterpolationType type : values())
        {
            if (type.function == interp)
            {
                return type;
            }
        }

        return LINEAR;
    }

    private InterpolationType(String name)
    {
        this.name = name;
    }

    private InterpolationType(String name, KeyframeInterpolation interp)
    {
        this(name, interp, KeyframeEasing.IN);
    }

    private InterpolationType(String name, KeyframeInterpolation interp, int keybind)
    {
        this(name, interp, KeyframeEasing.IN);

        this.keybind = keybind;
    }

    private InterpolationType(String name, KeyframeInterpolation interp, KeyframeEasing easing)
    {
        this.name = name;
        this.interp = interp;
        this.easing = easing;
    }

    private InterpolationType(Interpolation function)
    {
        this.name = function.key;
        this.function = function;
    }

    private InterpolationType(Interpolation function, KeyframeInterpolation interp)
    {
        this(function, interp, KeyframeEasing.IN);
    }

    private InterpolationType(Interpolation function, KeyframeInterpolation interp, KeyframeEasing easing)
    {
        this.name = function.key;
        this.function = function;
        this.interp = interp;
        this.easing = easing;
    }

    public void setupKeybind(ContextAction action, IKey category)
    {
        if (this.function != null)
        {
            this.function.setupKeybind(action, category);
        }
        else
        {
            action.key(category, this.keybind);
        }
    }

    public IKey getName()
    {
        return UIKeys.C_INTERPOLATION.get(this.name);
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
