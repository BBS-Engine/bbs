package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIBooleanKeyframeFactory extends UIKeyframeFactory<Boolean>
{
    private UIToggle toggle;

    public UIBooleanKeyframeFactory(GenericKeyframe<Boolean> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.toggle = new UIToggle(IKey.lazy("True"), (b) -> this.editor.setValue(b.getValue()));
        this.toggle.setValue(keyframe.getValue());

        this.add(this.toggle);
    }
}