package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UILinkKeyframeFactory extends UIKeyframeFactory<Link>
{
    public UILinkKeyframeFactory(GenericKeyframe<Link> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        UIButton button = new UIButton(IKey.lazy("Pick texture..."), (b) ->
        {
            UITexturePicker.open(this.getParentContainer(), this.keyframe.getValue(), this.keyframe::setValue);
        });

        this.add(button);
    }
}