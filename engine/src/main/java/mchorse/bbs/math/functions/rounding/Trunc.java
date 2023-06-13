package mchorse.bbs.math.functions.rounding;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Trunc extends NNFunction
{
    public Trunc(IExpression[] expressions, String name) throws Exception
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
        double value = this.getArg(0).doubleValue();

        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }
}