package mchorse.bbs.math.molang.expressions;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangParser;

public abstract class MolangExpression
{
    public MolangParser context;

    public static boolean isZero(MolangExpression expression)
    {
        return isConstant(expression, 0);
    }

    public static boolean isOne(MolangExpression expression)
    {
        return isConstant(expression, 1);
    }

    public static boolean isConstant(MolangExpression expression, double x)
    {
        if (expression instanceof MolangValue)
        {
            MolangValue value = (MolangValue) expression;

            return value.expression instanceof Constant && Operation.equals(value.expression.get().doubleValue(), x);
        }

        return false;
    }

    public static boolean isExpressionConstant(MolangExpression expression)
    {
        if (expression instanceof MolangValue)
        {
            MolangValue value = (MolangValue) expression;

            return value.expression instanceof Constant;
        }

        return false;
    }

    public MolangExpression(MolangParser context)
    {
        this.context = context;
    }

    public abstract double get();

    public BaseType toData()
    {
        return new StringType(this.toString());
    }
}