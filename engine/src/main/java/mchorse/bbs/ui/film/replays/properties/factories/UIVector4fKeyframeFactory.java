package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import org.joml.Vector4f;

public class UIVector4fKeyframeFactory extends UIKeyframeFactory<Vector4f>
{
    private UITrackpad x;
    private UITrackpad y;
    private UITrackpad z;
    private UITrackpad w;

    public UIVector4fKeyframeFactory(GenericKeyframe<Vector4f> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.x = new UITrackpad((v) -> this.editor.setValue(this.getValue()));
        this.x.setValue(keyframe.value.x);
        this.y = new UITrackpad((v) -> this.editor.setValue(this.getValue()));
        this.y.setValue(keyframe.value.y);
        this.z = new UITrackpad((v) -> this.editor.setValue(this.getValue()));
        this.z.setValue(keyframe.value.z);
        this.w = new UITrackpad((v) -> this.editor.setValue(this.getValue()));
        this.w.setValue(keyframe.value.w);

        this.add(UI.row(this.x, this.y), UI.row(this.z, this.w));
    }

    private Vector4f getValue()
    {
        return new Vector4f(
            (float) this.x.getValue(), (float) this.y.getValue(),
            (float) this.z.getValue(), (float) this.w.getValue()
        );
    }
}