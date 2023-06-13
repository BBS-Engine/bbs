package mchorse.bbs.math.functions;

import mchorse.bbs.math.IExpression;

/**
 * Function that expects number input arguments and outputs a string
 */
public abstract class NSFunction extends Function
{
    public NSFunction(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);

        for (IExpression expression : expressions)
        {
            if (!expression.isNumber())
            {
                throw new IllegalStateException("Function " + name + " cannot receive string arguments!");
            }
        }
    }

    @Override
    protected void verifyArgument(int index, IExpression expression)
    {
        if (!expression.isNumber())
        {
            throw new IllegalStateException("Function " + this.name + " cannot receive string arguments!");
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
