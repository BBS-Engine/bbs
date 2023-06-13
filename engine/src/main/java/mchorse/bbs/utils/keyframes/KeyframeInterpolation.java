package mchorse.bbs.utils.keyframes;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.context.ContextAction;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.lwjgl.glfw.GLFW;

public enum KeyframeInterpolation
{
    CONST("const")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.CONSTANT;
        }

        @Override
        public void setupKeybind(ContextAction action, IKey category)
        {
            action.key(category, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
    },
    LINEAR("linear")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return Interpolation.LINEAR;
        }
    },
    QUAD("quad")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUAD_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUAD_OUT;

            return Interpolation.QUAD_INOUT;
        }
    },
    CUBIC("cubic")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.CUBIC_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.CUBIC_OUT;

            return Interpolation.CUBIC_INOUT;
        }
    },
    HERMITE("hermite")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.HERMITE;
        }

        @Override
        public double interpolate(Keyframe a, Keyframe b, double x)
        {
            double v0 = a.prev.value;
            double v1 = a.value;
            double v2 = b.value;
            double v3 = b.next.value;

            return Interpolations.cubicHermite(v0, v1, v2, v3, x);
        }

        @Override
        public void setupKeybind(ContextAction action, IKey category)
        {
            action.key(category, GLFW.GLFW_KEY_H, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
    },
    EXP("exp")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.EXP_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.EXP_OUT;

            return Interpolation.EXP_INOUT;
        }
    },
    BEZIER("bezier")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            return KeyframeInterpolations.BEZIER;
        }

        @Override
        public double interpolate(Keyframe a, Keyframe b, double x)
        {
            if (x <= 0) return a.value;
            if (x >= 1) return b.value;

            /* Transform input to 0..1 */
            double w = b.tick - a.tick;
            double h = b.value - a.value;

            /* In case if there is no slope whatsoever */
            if (h == 0) h = 0.00001;

            double x1 = a.rx / w;
            double y1 = a.ry / h;
            double x2 = (w - b.lx) / w;
            double y2 = (h + b.ly) / h;
            double e = 0.0005;

            e = h == 0 ? e : Math.max(Math.min(e, 1 / h * e), 0.00001);
            x1 = MathUtils.clamp(x1, 0, 1);
            x2 = MathUtils.clamp(x2, 0, 1);

            return Interpolations.bezier(0, y1, y2, 1, Interpolations.bezierX(x1, x2, x, e)) * h + a.value;
        }

        @Override
        public void setupKeybind(ContextAction action, IKey category)
        {
            action.key(category, GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
    },
    BACK("back")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.BACK_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.BACK_OUT;

            return Interpolation.BACK_INOUT;
        }
    },
    ELASTIC("elastic")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.ELASTIC_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.ELASTIC_OUT;

            return Interpolation.ELASTIC_INOUT;
        }
    },
    BOUNCE("bounce")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.BOUNCE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.BOUNCE_OUT;

            return Interpolation.BOUNCE_INOUT;
        }
    },
    SINE("sine")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.SINE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.SINE_OUT;

            return Interpolation.SINE_INOUT;
        }
    },
    QUART("quart")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUART_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUART_OUT;

            return Interpolation.QUART_INOUT;
        }
    },
    QUINT("quint")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.QUINT_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.QUINT_OUT;

            return Interpolation.QUINT_INOUT;
        }
    },
    CIRCLE("circle")
    {
        @Override
        public IInterpolation from(KeyframeEasing easing)
        {
            if (easing == KeyframeEasing.IN) return Interpolation.CIRCLE_IN;
            if (easing == KeyframeEasing.OUT) return Interpolation.CIRCLE_OUT;

            return Interpolation.CIRCLE_INOUT;
        }
    };

    public final String key;

    private KeyframeInterpolation(String key)
    {
        this.key = key;
    }

    public IInterpolation from(KeyframeEasing easing)
    {
        return null;
    }

    public IKey getKey()
    {
        return UIKeys.C_INTERPOLATION.get(this.key);
    }

    public double interpolate(Keyframe a, Keyframe b, double x)
    {
        IInterpolation interpolation = this.from(a.easing);

        return interpolation == null ? a.value : interpolation.interpolate(a.value, b.value, x);
    }

    public void setupKeybind(ContextAction action, IKey category)
    {
        IInterpolation interpolation = this.from(KeyframeEasing.IN);

        if (interpolation instanceof Interpolation)
        {
            ((Interpolation) interpolation).setupKeybind(action, category);
        }
    }
}
