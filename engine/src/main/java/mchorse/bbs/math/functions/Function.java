package mchorse.bbs.math.functions;

import mchorse.bbs.math.Constant;
import mchorse.bbs.math.IExpression;

/**
 * Abstract function class
 * 
 * This class provides function capability (i.e. giving it arguments and 
 * upon {@link #get()} method you receive output).
 */
public abstract class Function implements IExpression
{
    protected IExpression[] args;
    protected String name;

    protected IExpression result = new Constant(0);

    public Function(IExpression[] expressions, String name) throws Exception
    {
        if (expressions.length < this.getRequiredArguments())
        {
            String message = String.format("Function '%s' requires at least %s arguments. %s are given!", this.getName(), this.getRequiredArguments(), expressions.length);

            throw new Exception(message);
        }

        for (int i = 0; i < expressions.length; i++)
        {
            this.verifyArgument(i, expressions[i]);
        }

        this.args = expressions;
        this.name = name;
    }

    protected void verifyArgument(int index, IExpression expression)
    {}

    @Override
    public void set(double value)
    {}

    @Override
    public void set(String value)
    {}

    /**
     * Get the value of nth argument 
     */
    public IExpression getArg(int index)
    {
        if (index < 0 || index >= this.args.length)
        {
            throw new IllegalStateException("Index should be within the argument's length range! Given " + index + ", arguments length: " +this.args.length);
        }

        return this.args[index].get();
    }

    @Override
    public String toString()
    {
        String args = "";

        for (int i = 0; i < this.args.length; i++)
        {
            args += this.args[i].toString();

            if (i < this.args.length - 1)
            {
                args += ", ";
            }
        }

        return this.getName() + "(" + args + ")";
    }

    /**
     * Get name of this function 
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get minimum count of arguments this function needs
     */
    public int getRequiredArguments()
    {
        return 0;
    }
}