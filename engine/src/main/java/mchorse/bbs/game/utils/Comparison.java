package mchorse.bbs.game.utils;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.math.Variable;

public class Comparison implements IMapSerializable
{
    private static final MathBuilder MATH;
    private static final Variable VALUE;
    private static final Variable VALUE2;

    public ComparisonMode comparison = ComparisonMode.EQUALS;
    public double value;
    public String expression = "";

    private IExpression compiledValue;

    static
    {
        VALUE = new Variable("value", 0);
        VALUE2 = new Variable("x", 0);

        MATH = new MathBuilder();
        MATH.register(VALUE);
        MATH.register(VALUE2);
    }

    /**
     * Compare given value to expression or comparison mode
     */
    public boolean compare(double a)
    {
        if (this.comparison == ComparisonMode.EXPRESSION)
        {
            if (this.compiledValue == null)
            {
                try
                {
                    this.compiledValue = MATH.parse(this.expression);
                }
                catch (Exception e)
                {
                    this.compiledValue = new Constant(0);
                }
            }

            VALUE.set(a);
            VALUE2.set(a);

            return this.compiledValue.booleanValue();
        }

        return this.comparison.compare(a, this.value);
    }

    public String stringify(String id)
    {
        return this.comparison.stringify(id, this.value, this.expression);
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt("comparison", this.comparison.ordinal());
        data.putDouble("value", this.value);
        data.putString("expression", this.expression);
    }

    @Override
    public void fromData(MapType data)
    {
        this.compiledValue = null;

        this.comparison = EnumUtils.getValue(data.getInt("comparison"), ComparisonMode.values(), ComparisonMode.EQUALS);
        this.value = data.getDouble("value");
        this.expression = data.getString("expression");
    }
}