package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.FloatType;

public class FloatKeyframeSerializer implements IGenericKeyframeSerializer<Float>
{
    @Override
    public Float fromData(BaseType data)
    {
        return data.isNumeric() ? data.asNumeric().floatValue() : 0F;
    }

    @Override
    public BaseType toData(Float value)
    {
        return new FloatType(value);
    }

    @Override
    public Float copy(Float value)
    {
        return value;
    }
}