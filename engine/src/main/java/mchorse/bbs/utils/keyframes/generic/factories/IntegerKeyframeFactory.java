package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIIntegerKeyframeFactory;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

public class IntegerKeyframeFactory implements IGenericKeyframeFactory<Integer>
{
    @Override
    public Integer fromData(BaseType data)
    {
        return data.isNumeric() ? data.asNumeric().intValue() : 0;
    }

    @Override
    public BaseType toData(Integer value)
    {
        return new IntType(value);
    }

    @Override
    public Integer copy(Integer value)
    {
        return value;
    }

    @Override
    public Integer interpolate(Integer a, Integer b, IInterpolation interpolation, float x)
    {
        return (int) interpolation.interpolate(a.intValue(), b.intValue(), x);
    }

    @Override
    public UIKeyframeFactory<Integer> createUI(GenericKeyframe<Integer> keyframe, UIPropertyEditor editor)
    {
        return new UIIntegerKeyframeFactory(keyframe, editor);
    }
}