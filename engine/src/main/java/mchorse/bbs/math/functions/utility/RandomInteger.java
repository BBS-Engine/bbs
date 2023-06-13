package mchorse.bbs.math.functions.utility;

import mchorse.bbs.math.IExpression;

public class RandomInteger extends Random
{
    public RandomInteger(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}