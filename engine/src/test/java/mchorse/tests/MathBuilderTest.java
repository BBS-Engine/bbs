package mchorse.tests;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.math.Variable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathBuilderTest
{
    @Test
    public void testParsing()
    {
        MathBuilder builder = new MathBuilder();
        Variable t = new Variable("t", 0);

        builder.register(t);

        try
        {
            IExpression expression = builder.parse("-(t + 10)");

            Assertions.assertEquals("-(t + 10.0)", expression.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();

            Assertions.fail();
        }
    }

    @Test
    public void testParsingMinusInFront()
    {
        MathBuilder builder = new MathBuilder();
        IExpression expression = null;

        try
        {
            expression = builder.parse("- sin(0)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Assertions.assertNotNull(expression);
    }
}