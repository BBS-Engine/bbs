package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIIntegerKeyframeFactory extends UIKeyframeFactory<Integer>
{
    private UITrackpad value;

    public UIIntegerKeyframeFactory(GenericKeyframe<Integer> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.value = new UITrackpad((v) -> this.editor.setValue(v.intValue()));
        this.value.integer().setValue(keyframe.getValue());

        this.add(this.value);
    }
}