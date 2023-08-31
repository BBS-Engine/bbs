package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UITransformKeyframeFactory;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

public class TransformKeyframeFactory implements IGenericKeyframeFactory<Transform>
{
    private Transform i = new Transform();

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

    @Override
    public Transform interpolate(Transform a, Transform b, IInterpolation interpolation, float x)
    {
        float factor = interpolation.interpolate(0, 1, x);

        this.i.copy(a);
        this.i.lerp(b, factor);

        return this.i;
    }

    @Override
    public UIKeyframeFactory<Transform> createUI(GenericKeyframe<Transform> keyframe, UIPropertyEditor editor)
    {
        return new UITransformKeyframeFactory(keyframe, editor);
    }
}