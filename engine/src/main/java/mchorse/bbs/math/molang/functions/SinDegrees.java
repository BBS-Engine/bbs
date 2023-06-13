package mchorse.bbs.math.molang.functions;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class SinDegrees extends NNFunction
{
    public SinDegrees(IExpression[] expressions, String name) throws Exception
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
        return Math.sin(this.getArg(0).doubleValue() / 180 * Math.PI);
    }
}