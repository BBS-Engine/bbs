package mchorse.bbs.math.functions.classic;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Pow extends NNFunction
{
    public Pow(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double doubleValue()
    {
        return Math.pow(this.getArg(0).doubleValue(), this.getArg(1).doubleValue());
    }
}