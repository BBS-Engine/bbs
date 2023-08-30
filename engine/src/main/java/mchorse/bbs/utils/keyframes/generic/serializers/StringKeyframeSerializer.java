package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;

public class StringKeyframeSerializer implements IGenericKeyframeSerializer<String>
{
    @Override
    public String fromData(BaseType data)
    {
        return data.isString() ? data.asString() : "";
    }

    @Override
    public BaseType toData(String value)
    {
        return new StringType(value);
    }

    @Override
    public String copy(String value)
    {
        return value;
    }
}