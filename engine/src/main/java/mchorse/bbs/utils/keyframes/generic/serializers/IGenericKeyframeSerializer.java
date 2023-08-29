package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;

public interface IGenericKeyframeSerializer <T>
{
    public T fromData(BaseType data);

    public BaseType toData(T value);

    public T copy(T value);
}