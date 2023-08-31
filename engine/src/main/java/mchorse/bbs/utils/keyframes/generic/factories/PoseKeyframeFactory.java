package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UIPoseKeyframeFactory;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class PoseKeyframeFactory implements IGenericKeyframeFactory<Pose>
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

    @Override
    public Pose create()
    {
        return new Pose();
    }

    @Override
    public UIKeyframeFactory<Pose> createUI(GenericKeyframe<Pose> keyframe, UIPropertyEditor editor)
    {
        return new UIPoseKeyframeFactory(keyframe, editor);
    }
}