package mchorse.bbs.math;

/**
 * Variable class
 * 
 * This class is responsible for providing a mutable {@link IExpression}
 * which can be modifier during runtime and still getting referenced in 
 * the expressions parsed by {@link MathBuilder}.
 * 
 * But in practice, it's simply returns stored value and provides a 
 * method to modify it.
 */
public class Variable extends Constant
{
    private String name;

    public Variable(String name, double value)
    {
        super(value);

        this.name = name;
    }

    public Variable(String name, String value)
    {
        super(value);

        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}