package mchorse.bbs.math.functions.utility;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class HermiteBlend extends NNFunction
{
    public HermiteBlend(IExpression[] expressions, String name) throws Exception
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
        double x = this.getArg(0).doubleValue();

        return 3 * x * x - 2 * x * x * x;
    }
}