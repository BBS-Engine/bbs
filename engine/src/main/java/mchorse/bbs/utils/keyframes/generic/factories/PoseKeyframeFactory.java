package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIKeyframeFactory;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIPoseKeyframeFactory;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

import java.util.HashSet;
import java.util.Set;

public class PoseKeyframeFactory implements IGenericKeyframeFactory<Pose>
{
    private static Set<String> keys = new HashSet<>();

    private Pose i = new Pose();

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
    public Pose interpolate(Pose a, Pose b, IInterpolation interpolation, float x)
    {
        float factor = interpolation.interpolate(0, 1, x);

        keys.clear();

        if (a != null)
        {
            keys.addAll(a.transforms.keySet());
        }

        if (b != null)
        {
            keys.addAll(b.transforms.keySet());
        }

        this.i.copy(a);

        for (String key : keys)
        {
            Transform transform = this.i.get(key);
            Transform t = b.get(key);

            transform.lerp(t, factor);
        }

        return this.i;
    }

    @Override
    public UIKeyframeFactory<Pose> createUI(GenericKeyframe<Pose> keyframe, UIPropertyEditor editor)
    {
        return new UIPoseKeyframeFactory(keyframe, editor);
    }
}