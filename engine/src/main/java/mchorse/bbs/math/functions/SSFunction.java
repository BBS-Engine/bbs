package mchorse.bbs.math.functions;

import mchorse.bbs.math.IExpression;

/**
 * Function that expects string input arguments and outputs a string
 */
public abstract class SSFunction extends Function
{
    public SSFunction(IExpression[] expressions, String name) throws Exception
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
        this.result.set(this.stringValue());

        return this.result;
    }

    @Override
    public boolean isNumber()
    {
        return false;
    }

    @Override
    public double doubleValue()
    {
        return 0;
    }

    @Override
    public boolean booleanValue()
    {
        return this.stringValue().equalsIgnoreCase("true");
    }
}
