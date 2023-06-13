package mchorse.bbs.math.functions.trig;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Acos extends NNFunction
{
    public Acos(IExpression[] expressions, String name) throws Exception
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
        return Math.acos(this.getArg(0).doubleValue());
    }
}
