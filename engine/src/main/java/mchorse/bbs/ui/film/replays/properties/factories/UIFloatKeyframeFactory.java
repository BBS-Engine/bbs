package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIFloatKeyframeFactory extends UIKeyframeFactory<Float>
{
    private UITrackpad value;

    public UIFloatKeyframeFactory(GenericKeyframe<Float> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.value = new UITrackpad((v) -> this.editor.setValue(v.floatValue()));
        this.value.setValue(keyframe.getTick());

        this.add(this.value);
    }
}