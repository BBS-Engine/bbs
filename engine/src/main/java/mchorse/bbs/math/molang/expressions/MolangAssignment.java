package mchorse.bbs.math.molang.expressions;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.Variable;
import mchorse.bbs.math.molang.MolangParser;

public class MolangAssignment extends MolangExpression
{
    public Variable variable;
    public IExpression expression;

    public MolangAssignment(MolangParser context, Variable variable, IExpression expression)
    {
        super(context);

        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public double get()
    {
        double value = this.expression.get().doubleValue();

        this.variable.set(value);

        return value;
    }

    @Override
    public String toString()
    {
        return this.variable.getName() + " = " + this.expression.toString();
    }
}