package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIBooleanKeyframeFactory;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

public class BooleanKeyframeFactory implements IGenericKeyframeFactory<Boolean>
{
    @Override
    public Boolean fromData(BaseType data)
    {
        return data.isNumeric() && data.asNumeric().boolValue();
    }

    @Override
    public BaseType toData(Boolean value)
    {
        return new ByteType(value);
    }

    @Override
    public Boolean copy(Boolean value)
    {
        return value;
    }

    @Override
    public Boolean interpolate(Boolean a, Boolean b, IInterpolation interpolation, float x)
    {
        return b;
    }

    @Override
    public UIKeyframeFactory<Boolean> createUI(GenericKeyframe<Boolean> keyframe, UIPropertyEditor editor)
    {
        return new UIBooleanKeyframeFactory(keyframe, editor);
    }
}