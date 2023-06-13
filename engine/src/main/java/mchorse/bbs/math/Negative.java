package mchorse.bbs.math;

/**
 * Negative operator class
 *
 * This class is responsible for inverting given value
 */
public class Negative extends Wrapper
{
    public Negative(IExpression expression)
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
        return -this.expression.doubleValue();
    }

    @Override
    public boolean booleanValue()
    {
        return Operation.isTrue(this.doubleValue());
    }

    @Override
    public String toString()
    {
        return "-" + this.expression.toString();
    }
}