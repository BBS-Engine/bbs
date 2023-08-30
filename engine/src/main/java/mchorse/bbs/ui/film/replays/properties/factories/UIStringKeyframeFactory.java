package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIStringKeyframeFactory extends UIKeyframeFactory<String>
{
    private UITextbox string;

    public UIStringKeyframeFactory(GenericKeyframe<String> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.string = new UITextbox(1000, (t) -> this.editor.setValue(t));
        this.string.setText(keyframe.value);

        this.add(this.string);
    }
}