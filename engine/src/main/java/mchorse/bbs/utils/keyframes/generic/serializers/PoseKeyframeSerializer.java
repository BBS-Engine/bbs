package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.utils.Pose;

public class PoseKeyframeSerializer implements IGenericKeyframeSerializer<Pose>
{
    @Override
    public Pose fromData(BaseType data)
    {
        Pose pose = new Pose();

        if (data.isMap())
        {
            pose.fromData(data.asMap());
        }

        return pose;
    }

    @Override
    public BaseType toData(Pose value)
    {
        return value.toData();
    }

    @Override
    public Pose copy(Pose value)
    {
        return value.copy();
    }
}