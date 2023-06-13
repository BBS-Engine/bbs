package mchorse.bbs.math.molang.functions;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.trig.Atan2;

public class Atan2Degrees extends Atan2
{
    public Atan2Degrees(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}