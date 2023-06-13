package mchorse.bbs.math.functions.classic;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

/**
 * Absolute value function 
 */
public class Abs extends NNFunction
{
    public Abs(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        return Math.abs(this.getArg(0).doubleValue());
    }
}