package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public abstract class UIKeyframeFactory <T> extends UIElement
{
    protected GenericKeyframe<T> keyframe;
    protected UIPropertyEditor editor;

    public UIKeyframeFactory(GenericKeyframe<T> keyframe, UIPropertyEditor editor)
    {
        this.keyframe = keyframe;
        this.editor = editor;

        this.column().vertical().stretch();
    }
}