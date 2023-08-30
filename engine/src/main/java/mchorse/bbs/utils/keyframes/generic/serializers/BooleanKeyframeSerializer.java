package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;

public class BooleanKeyframeSerializer implements IGenericKeyframeSerializer<Boolean>
{
    @Override
    public Boolean fromData(BaseType data)
    {
        return data.isNumeric() && data.asNumeric().boolValue();
    }

    @Override
    public BaseType toData(Boolean value)
    {
        return new ByteType(value);
    }

    @Override
    public Boolean copy(Boolean value)
    {
        return value;
    }
}