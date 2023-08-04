package mchorse.bbs.camera.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.settings.values.base.BaseValue;

public class ValueExpression extends BaseValue
{
    public IExpression expression;
    public MathBuilder builder;
    public boolean lastError;

    public ValueExpression(String id, MathBuilder builder)
    {
        super(id);

        this.builder = builder;
    }

    public boolean isErrored()
    {
        return this.lastError;
    }

    public IExpression get()
    {
        return this.expression;
    }

    public void set(String expression) throws Exception
    {
        this.expression = this.builder.parse(expression);
    }

    public void setExpression(String string)
    {
        try
        {
            if (string.isEmpty())
            {
                this.expression = null;
            }
            else
            {
                this.set(string);
            }

            this.lastError = false;
        }
        catch (Exception e)
        {
            this.expression = null;
            this.lastError = true;
        }
    }

    @Override
    public void reset()
    {
        this.expression = null;
        this.lastError = false;
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.expression == null ? "" : this.expression.toString());
    }

    @Override
    public void fromData(BaseType data)
    {
        this.setExpression(data.asString());
    }

    @Override
    public String toString()
    {
        return this.expression == null ? "" : this.expression.toString();
    }
}