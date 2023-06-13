package mchorse.bbs.math.molang.expressions;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.DoubleType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.molang.MolangParser;

public class MolangValue extends MolangExpression
{
    public IExpression expression;
    public boolean returns;

    public MolangValue(MolangParser context, IExpression expression)
    {
        super(context);

        this.expression = expression;
    }

    public MolangExpression addReturn()
    {
        this.returns = true;

        return this;
    }

    @Override
    public double get()
    {
        return this.expression.get().doubleValue();
    }

    @Override
    public String toString()
    {
        return (this.returns ? MolangParser.RETURN : "") + this.expression.toString();
    }

    @Override
    public BaseType toData()
    {
        if (this.expression instanceof Constant)
        {
            return new DoubleType(this.expression.get().doubleValue());
        }

        return super.toData();
    }
}