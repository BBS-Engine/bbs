package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UIVector4fKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;
import org.joml.Vector4f;

public class Vector4fKeyframeFactory implements IGenericKeyframeFactory<Vector4f>
{
    private Vector4f i = new Vector4f();

    @Override
    public Vector4f fromData(BaseType data)
    {
        return data.isList() ? DataStorageUtils.vector4fFromData(data.asList()) : new Vector4f();
    }

    @Override
    public BaseType toData(Vector4f value)
    {
        return DataStorageUtils.vector4fToData(value);
    }

    @Override
    public Vector4f copy(Vector4f value)
    {
        return new Vector4f(value);
    }

    @Override
    public Vector4f interpolate(Vector4f a, Vector4f b, IInterpolation interpolation, float x)
    {
        float factor = interpolation.interpolate(0, 1, x);

        a.lerp(b, factor, this.i);

        return this.i;
    }

    @Override
    public UIKeyframeFactory<Vector4f> createUI(GenericKeyframe<Vector4f> keyframe, UIPropertyEditor editor)
    {
        return new UIVector4fKeyframeFactory(keyframe, editor);
    }
}