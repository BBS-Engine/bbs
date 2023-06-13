package mchorse.bbs.math;

/**
 * Group class
 * 
 * Simply wraps given {@link IExpression} into parenthesis in the
 * {@link #toString()} method.
 */
public class Group implements IExpression
{
    private IExpression expression;

    public Group(IExpression expression)
    {
        this.expression = expression;
    }

    @Override
    public IExpression get()
    {
        return this.expression.get();
    }

    @Override
    public boolean isNumber()
    {
        return this.expression.isNumber();
    }

    @Override
    public void set(double value)
    {
        this.expression.set(value);
    }

    @Override
    public void set(String value)
    {
        this.expression.set(value);
    }

    @Override
    public double doubleValue()
    {
        return this.expression.doubleValue();
    }

    @Override
    public boolean booleanValue()
    {
        return this.expression.booleanValue();
    }

    @Override
    public String stringValue()
    {
        return this.expression.stringValue();
    }

    @Override
    public String toString()
    {
        return "(" + this.expression.toString() + ")";
    }
}