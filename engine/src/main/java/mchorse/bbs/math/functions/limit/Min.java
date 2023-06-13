package mchorse.bbs.math.functions.limit;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Min extends NNFunction
{
    public Min(IExpression[] expressions, String name) throws Exception
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
        return Math.min(this.getArg(0).doubleValue(), this.getArg(1).doubleValue());
    }
}