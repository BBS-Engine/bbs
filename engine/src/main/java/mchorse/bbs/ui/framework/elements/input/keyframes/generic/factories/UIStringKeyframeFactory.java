package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIStringKeyframeFactory extends UIKeyframeFactory<String>
{
    private UITextbox string;

    public UIStringKeyframeFactory(GenericKeyframe<String> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.string = new UITextbox(1000, (t) -> this.editor.setValue(t));
        this.string.setText(keyframe.getValue());

        this.add(this.string);
    }
}