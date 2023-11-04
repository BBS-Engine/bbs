package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIBooleanKeyframeFactory extends UIKeyframeFactory<Boolean>
{
    private UIToggle toggle;

    public UIBooleanKeyframeFactory(GenericKeyframe<Boolean> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.toggle = new UIToggle(UIKeys.GENERIC_KEYFRAMES_BOOLEAN_TRUE, (b) -> this.editor.setValue(b.getValue()));
        this.toggle.setValue(keyframe.getValue());

        this.add(this.toggle);
    }
}