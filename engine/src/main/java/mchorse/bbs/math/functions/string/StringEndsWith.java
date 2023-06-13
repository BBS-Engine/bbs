package mchorse.bbs.math.functions.string;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.SNFunction;

public class StringEndsWith extends SNFunction
{
    public StringEndsWith(IExpression[] expressions, String name) throws Exception
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
        return this.getArg(0).stringValue().endsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
