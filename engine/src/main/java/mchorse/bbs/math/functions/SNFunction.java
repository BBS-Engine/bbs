package mchorse.bbs.math.functions;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.Operation;

/**
 * Function that expects string input arguments and outputs a number
 */
public abstract class SNFunction extends Function
{
    public SNFunction(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    protected void verifyArgument(int index, IExpression expression)
    {
        if (expression.isNumber())
        {
            throw new IllegalStateException("Function " + this.name + " cannot receive number arguments!");
        }
    }

    @Override
    public IExpression get()
    {
        this.result.set(this.doubleValue());

        return this.result;
    }

    @Override
    public boolean isNumber()
    {
        return true;
    }

    @Override
    public boolean booleanValue()
    {
        return Operation.isTrue(this.doubleValue());
    }

    @Override
    public String stringValue()
    {
        return "";
    }
}
