package mchorse.bbs.math.molang.functions;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.functions.trig.Atan;

public class AtanDegrees extends Atan
{
    public AtanDegrees(IExpression[] expressions, String name) throws Exception
    {
        super(expressions, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}