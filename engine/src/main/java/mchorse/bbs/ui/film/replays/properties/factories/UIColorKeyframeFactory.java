package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIColorKeyframeFactory extends UIKeyframeFactory<Color>
{
    private UIColor color;

    public UIColorKeyframeFactory(GenericKeyframe<Color> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.color = new UIColor((c) -> this.editor.setValue(Color.rgba(c.intValue())));
        this.color.setColor(keyframe.getValue().getARGBColor());
        this.color.withAlpha();

        this.add(this.color);
    }
}