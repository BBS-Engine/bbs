package mchorse.bbs.game.utils;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.Operation;
import mchorse.bbs.ui.UIKeys;

public enum ComparisonMode
{
    LESS(Operation.LESS), LESS_THAN(Operation.LESS_THAN), EQUALS(Operation.EQUALS), GREATER_THAN(Operation.GREATER_THAN), GREATER(Operation.GREATER),
    IS_TRUE(null)
    {
        @Override
        public boolean compare(double a, double b)
        {
            return Operation.isTrue(a);
        }

        @Override
        public String stringify(String a, double b, String expression)
        {
            return a + " == true";
        }

        @Override
        public IKey stringify()
        {
            return UIKeys.CONDITIONS_COMPARISONS_IS_TRUE;
        }
    },
    IS_FALSE(null)
    {
        @Override
        public boolean compare(double a, double b)
        {
            return !Operation.isTrue(a);
        }

        @Override
        public String stringify(String a, double b, String expression)
        {
            return a + " == false";
        }

        @Override
        public IKey stringify()
        {
            return UIKeys.CONDITIONS_COMPARISONS_IS_FALSE;
        }
    },
    EXPRESSION(null)
    {
        @Override
        public String stringify(String a, double b, String expression)
        {
            return expression;
        }

        @Override
        public IKey stringify()
        {
            return UIKeys.CONDITIONS_EXPRESSION;
        }
    };

    public final Operation operation;

    private ComparisonMode(Operation operation)
    {
        this.operation = operation;
    }

    public boolean compare(double a, double b)
    {
        return this.operation.calculate(a, b) == 1;
    }

    public String stringify(String a, double b, String expression)
    {
        return a + " " + this.operation.sign + " " + b;
    }

    public IKey stringify()
    {
        return IKey.raw(this.operation.sign);
    }
}