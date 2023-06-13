package mchorse.bbs.math.functions.limit;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;
import mchorse.bbs.utils.math.MathUtils;

public class Clamp extends NNFunction
{
    public Clamp(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double doubleValue()
    {
        return MathUtils.clamp(this.getArg(0).doubleValue(), this.getArg(1).doubleValue(), this.getArg(2).doubleValue());
    }
}