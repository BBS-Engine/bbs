package mchorse.bbs.math.functions.classic;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.NNFunction;

public class Ln extends NNFunction
{
    public Ln(IExpression[] expressions, String name) throws Exception
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
        return Math.log(this.getArg(0).doubleValue());
    }
}