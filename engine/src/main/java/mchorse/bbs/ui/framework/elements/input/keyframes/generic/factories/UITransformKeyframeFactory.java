package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UITransformKeyframeFactory extends UIKeyframeFactory<Transform>
{
    private UIPropTransform transform;

    public UITransformKeyframeFactory(GenericKeyframe<Transform> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.transform = new UIPropTransform(keyframe::setValue);
        this.transform.verticalCompact();
        this.transform.setTransform(keyframe.getValue());

        this.add(this.transform);
    }
}