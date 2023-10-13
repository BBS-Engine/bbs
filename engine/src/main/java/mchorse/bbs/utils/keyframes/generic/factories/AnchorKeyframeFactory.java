package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.forms.properties.AnchorProperty;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIAnchorKeyframeFactory;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

public class AnchorKeyframeFactory implements IGenericKeyframeFactory<AnchorProperty.Anchor>
{
    @Override
    public AnchorProperty.Anchor fromData(BaseType data)
    {
        AnchorProperty.Anchor anchor = new AnchorProperty.Anchor();

        anchor.fromData(data.asMap());

        return anchor;
    }

    @Override
    public BaseType toData(AnchorProperty.Anchor value)
    {
        return value.toData();
    }

    @Override
    public AnchorProperty.Anchor copy(AnchorProperty.Anchor value)
    {
        AnchorProperty.Anchor anchor = new AnchorProperty.Anchor();

        anchor.actor = value.actor;
        anchor.attachment = value.attachment;

        return anchor;
    }

    @Override
    public AnchorProperty.Anchor interpolate(AnchorProperty.Anchor a, AnchorProperty.Anchor b, IInterpolation interpolation, float x)
    {
        return b;
    }

    @Override
    public UIKeyframeFactory<AnchorProperty.Anchor> createUI(GenericKeyframe<AnchorProperty.Anchor> keyframe, UIPropertyEditor editor)
    {
        return new UIAnchorKeyframeFactory(keyframe, editor);
    }
}