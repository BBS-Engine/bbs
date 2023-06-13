package mchorse.bbs.math;

public abstract class Wrapper implements IExpression
{
    public IExpression expression;

    protected IExpression result = new Constant(0);

    public Wrapper(IExpression expression)
    {
        this.expression = expression;
    }

    @Override
    public IExpression get()
    {
        this.process();

        return this.result;
    }

    protected abstract void process();

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
}
