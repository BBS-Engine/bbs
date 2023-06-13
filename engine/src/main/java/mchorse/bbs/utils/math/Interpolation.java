package mchorse.bbs.utils.math;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.context.ContextAction;
import org.lwjgl.glfw.GLFW;

public enum Interpolation implements IInterpolation
{
    LINEAR("linear", GLFW.GLFW_KEY_L)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return Interpolations.lerp(a, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.lerp(a, b, x);
        }
    },
    QUAD_IN("quad_in", GLFW.GLFW_KEY_Q)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a + (b - a) * x * x;
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a + (b - a) * x * x;
        }
    },
    QUAD_OUT("quad_out", GLFW.GLFW_KEY_Q)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a - (b - a) * x * (x - 2);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a - (b - a) * x * (x - 2);
        }
    },
    QUAD_INOUT("quad_inout", GLFW.GLFW_KEY_Q)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * x * x;

            x -= 1;

            return a - (b - a) / 2 * (x * (x - 2) - 1);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * x * x;

            x -= 1;

            return a - (b - a) / 2 * (x * (x - 2) - 1);
        }
    },
    CUBIC_IN("cubic_in", GLFW.GLFW_KEY_C)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a + (b - a) * x * x * x;
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a + (b - a) * x * x * x;
        }
    },
    CUBIC_OUT("cubic_out", GLFW.GLFW_KEY_C)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x -= 1;
            return a + (b - a) * (x * x * x + 1);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x -= 1;
            return a + (b - a) * (x * x * x + 1);
        }
    },
    CUBIC_INOUT("cubic_inout", GLFW.GLFW_KEY_C)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * x * x * x;

            x -= 2;

            return a + (b - a) / 2 * (x * x * x + 2);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * x * x * x;

            x -= 2;

            return a + (b - a) / 2 * (x * x * x + 2);
        }
    },
    EXP_IN("exp_in", GLFW.GLFW_KEY_E)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a + (b - a) * (float) Math.pow(2, 10 * (x - 1));
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a + (b - a) * Math.pow(2, 10 * (x - 1));
        }
    },
    EXP_OUT("exp_out", GLFW.GLFW_KEY_E)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a + (b - a) * (float) remapExp(-Math.pow(2, -10 * x) + 1);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a + (b - a) * remapExp(-Math.pow(2, -10 * x) + 1);
        }
    },
    EXP_INOUT("exp_inout", GLFW.GLFW_KEY_E)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            if (x == 0) return a;
            if (x == 1) return b;

            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * (float) Math.pow(2, 10 * (x - 1));

            x -= 1;

            return a + (b - a) / 2 * (float) (-remapExp(Math.pow(2, -10 * x)) + 2);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            if (x == 0) return a;
            if (x == 1) return b;

            x *= 2;

            if (x < 1F) return a + (b - a) / 2 * Math.pow(2, 10 * (x - 1));

            x -= 1;

            return a + (b - a) / 2 * (-remapExp(Math.pow(2, -10 * x)) + 2);
        }
    },
    /* Following interpolations below were copied from: https://easings.net/ */
    BACK_IN("back_in", GLFW.GLFW_KEY_B)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c3 = c1 + 1;

            return Interpolations.lerp(a, b, c3 * x * x * x - c1 * x * x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c3 = c1 + 1;

            return Interpolations.lerp(a, b, c3 * x * x * x - c1 * x * x);
        }
    },
    BACK_OUT("back_out", GLFW.GLFW_KEY_B)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c3 = c1 + 1;

            return Interpolations.lerp(a, b, 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2));
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c3 = c1 + 1;

            return Interpolations.lerp(a, b, 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
        }
    },
    BACK_INOUT("back_inout", GLFW.GLFW_KEY_B)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c2 = c1 * 1.525F;

            float factor = x < 0.5
                ? ((float) Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : ((float) Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c2 = c1 * 1.525D;

            double factor = x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_IN("elastic_in", GLFW.GLFW_KEY_S)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c4 = (2 * (float) Math.PI) / 3;

            float factor = x == 0 ? 0 :
                    (x == 1 ? 1 : -(float) Math.pow(2, 10 * x - 10) * (float) Math.sin((x * 10 - 10.75) * c4));

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c4 = (2 * Math.PI) / 3;

            double factor = x == 0 ? 0 :
                    (x == 1 ? 1 : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_OUT("elastic_out", GLFW.GLFW_KEY_S)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c4 = (2 * (float) Math.PI) / 3;

            float factor = x == 0 ? 0 :
                    (x == 1 ? 1 : (float) Math.pow(2, -10 * x) * (float) Math.sin((x * 10 - 0.75) * c4) + 1);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c4 = (2 * Math.PI) / 3;

            double factor = x == 0 ? 0 :
                (x == 1 ? 1 : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_INOUT("elastic_inout", GLFW.GLFW_KEY_S)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float c5 = (2 * (float) Math.PI) / 4.5F;

            float factor = x == 0 ? 0 : (x == 1 ? 1 :
                (x < 0.5
                    ? -((float) Math.pow(2, 20 * x - 10) * (float) Math.sin((20 * x - 11.125) * c5)) / 2
                    : ((float) Math.pow(2, -20 * x + 10) * (float) Math.sin((20 * x - 11.125) * c5)) / 2 + 1)
                );

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double c5 = (2 * Math.PI) / 4.5;

            double factor = x == 0 ? 0 : (x == 1 ? 1 :
                (x < 0.5
                    ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
                    : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1)
                );

            return Interpolations.lerp(a, b, factor);
        }
    },
    BOUNCE_IN("bounce_in", GLFW.GLFW_KEY_O)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return Interpolations.lerp(a, b, 1 - BOUNCE_OUT.interpolate(0, 1, 1 - x));
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.lerp(a, b, 1 - BOUNCE_OUT.interpolate(0, 1, 1 - x));
        }
    },
    BOUNCE_OUT("bounce_out", GLFW.GLFW_KEY_O)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            final float n1 = 7.5625F;
            final float d1 = 2.75F;
            float factor;

            if (x < 1 / d1)
            {
                factor = n1 * x * x;
            }
            else if (x < 2 / d1)
            {
                factor = n1 * (x -= 1.5F / d1) * x + 0.75F;
            }
            else if (x < 2.5 / d1)
            {
                factor = n1 * (x -= 2.25F / d1) * x + 0.9375F;
            }
            else
            {
                factor = n1 * (x -= 2.625F / d1) * x + 0.984375F;
            }

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            final double n1 = 7.5625;
            final double d1 = 2.75;
            double factor;

            if (x < 1 / d1)
            {
                factor = n1 * x * x;
            }
            else if (x < 2 / d1)
            {
                factor = n1 * (x -= 1.5 / d1) * x + 0.75;
            }
            else if (x < 2.5 / d1)
            {
                factor = n1 * (x -= 2.25 / d1) * x + 0.9375;
            }
            else
            {
                factor = n1 * (x -= 2.625 / d1) * x + 0.984375;
            }

            return Interpolations.lerp(a, b, factor);
        }
    },
    BOUNCE_INOUT("bounce_inout", GLFW.GLFW_KEY_O)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = x < 0.5
                ? (1 - BOUNCE_OUT.interpolate(0, 1, 1 - 2 * x)) / 2
                : (1 + BOUNCE_OUT.interpolate(0, 1, 2 * x - 1)) / 2;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = x < 0.5
                ? (1 - BOUNCE_OUT.interpolate(0, 1, 1 - 2 * x)) / 2
                : (1 + BOUNCE_OUT.interpolate(0, 1, 2 * x - 1)) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_IN("sine_in", GLFW.GLFW_KEY_I)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = 1 - (float) Math.cos((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = 1 - Math.cos((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_OUT("sine_out", GLFW.GLFW_KEY_I)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = (float) Math.sin((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = Math.sin((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_INOUT("sine_inout", GLFW.GLFW_KEY_I)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = (float) (-(Math.cos(Math.PI * x) - 1) / 2);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = -(Math.cos(Math.PI * x) - 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_IN("quart_in", GLFW.GLFW_KEY_U)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_OUT("quart_out", GLFW.GLFW_KEY_U)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = 1 - (float) Math.pow(1 - x, 4);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = 1 - Math.pow(1 - x, 4);

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_INOUT("quart_inout", GLFW.GLFW_KEY_U)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = x < 0.5 ? 8 * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 4) / 2;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_IN("quint_in", GLFW.GLFW_KEY_N)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = x * x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = x * x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_OUT("quint_out", GLFW.GLFW_KEY_N)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = 1 - (float) Math.pow(1 - x, 5);

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = 1 - Math.pow(1 - x, 5);

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_INOUT("quint_inout", GLFW.GLFW_KEY_N)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            float factor = x < 0.5 ? 16 * x * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 5) / 2;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            double factor = x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_IN("circle_in", GLFW.GLFW_KEY_R)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x = MathUtils.clamp(x, 0, 1);

            float factor = 1 - (float) Math.sqrt(1 - Math.pow(x, 2));

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x = MathUtils.clamp(x, 0, 1);

            double factor = 1 - (float) Math.sqrt(1 - Math.pow(x, 2));

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_OUT("circle_out", GLFW.GLFW_KEY_R)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x = MathUtils.clamp(x, 0, 1);

            float factor = (float) Math.sqrt(1 - Math.pow(x - 1, 2));

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x = MathUtils.clamp(x, 0, 1);

            double factor = Math.sqrt(1 - Math.pow(x - 1, 2));

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_INOUT("circle_inout", GLFW.GLFW_KEY_R)
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            x = MathUtils.clamp(x, 0, 1);

            float factor = x < 0.5
                ? (float) (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                : (float) (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            x = MathUtils.clamp(x, 0, 1);

            double factor = x < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    };

    public final String key;
    public final int keybind;

    private static double remapExp(double factor)
    {
        return (factor - 0.001D) * (1D / 0.999D);
    }

    private Interpolation(String key, int keybind)
    {
        this.key = key;
        this.keybind = keybind;
    }

    public void setupKeybind(ContextAction action, IKey category)
    {
        if (this.key.endsWith("_in"))
        {
            action.key(category, this.keybind, GLFW.GLFW_KEY_LEFT_SHIFT);
        }
        else if (this.key.endsWith("_out"))
        {
            action.key(category, this.keybind, GLFW.GLFW_KEY_LEFT_CONTROL);
        }
        else
        {
            action.key(category, this.keybind);
        }
    }

    public int getKeybind()
    {
        return -1;
    }

    public IKey getName()
    {
        return UIKeys.C_INTERPOLATION.get(this.key);
    }

    public IKey getTooltip()
    {
        return UIKeys.C_INTERPOLATION_TIPS.get(this.key);
    }
}