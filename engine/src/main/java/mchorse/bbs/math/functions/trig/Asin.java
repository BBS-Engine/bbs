package mchorse.bbs.math.functions.trig;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Asin extends NNFunction
{
    public Asin(IExpression[] expressions, String name) throws Exception
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
        return Math.asin(this.getArg(0).doubleValue());
    }
}
