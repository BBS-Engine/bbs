package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public abstract class UIKeyframeFactory <T> extends UIElement
{
    protected GenericKeyframe<T> keyframe;
    protected UIPropertyEditor editor;

    public UIKeyframeFactory(GenericKeyframe<T> keyframe, UIPropertyEditor editor)
    {
        this.keyframe = keyframe;
        this.editor = editor;

        this.column(5).vertical().stretch();
    }
}