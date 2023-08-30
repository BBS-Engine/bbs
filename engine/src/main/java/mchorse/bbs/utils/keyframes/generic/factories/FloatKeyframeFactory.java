package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIFloatKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class FloatKeyframeFactory implements IGenericKeyframeFactory<Float>
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

    @Override
    public Float create()
    {
        return 0F;
    }

    @Override
    public UIKeyframeFactory<Float> createUI(GenericKeyframe<Float> keyframe, UIPropertyEditor editor)
    {
        return new UIFloatKeyframeFactory(keyframe, editor);
    }
}