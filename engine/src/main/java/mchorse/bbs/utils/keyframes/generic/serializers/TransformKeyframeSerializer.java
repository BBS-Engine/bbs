package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.utils.Transform;

public class TransformKeyframeSerializer implements IGenericKeyframeSerializer<Transform>
{
    @Override
    public Transform fromData(BaseType data)
    {
        Transform transform = new Transform();

        if (data.isMap())
        {
            transform.fromData(data.asMap());
        }

        return transform;
    }

    @Override
    public BaseType toData(Transform value)
    {
        return value.toData();
    }

    @Override
    public Transform copy(Transform value)
    {
        return value.copy();
    }
}