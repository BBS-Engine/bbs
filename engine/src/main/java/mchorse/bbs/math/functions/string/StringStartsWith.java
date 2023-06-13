package mchorse.bbs.math.functions.string;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.SNFunction;

public class StringStartsWith extends SNFunction
{
    public StringStartsWith(IExpression[] expressions, String name) throws Exception
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
        return this.getArg(0).stringValue().startsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
