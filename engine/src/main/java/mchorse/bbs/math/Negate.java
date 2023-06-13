package mchorse.bbs.math;

/**
 * Negate operator class
 *
 * This class is responsible for negating given value
 */
public class Negate extends Wrapper
{
    public Negate(IExpression expression)
    {
        super(expression);
    }

    @Override
    protected void process()
    {
        this.result.set(this.doubleValue());
    }

    @Override
    public double doubleValue()
    {
        return this.booleanValue() ? 1 : 0;
    }

    @Override
    public boolean booleanValue()
    {
        return !this.expression.booleanValue();
    }

    @Override
    public String toString()
    {
        return "!" + this.expression.toString();
    }
}