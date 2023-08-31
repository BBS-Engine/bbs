package mchorse.bbs.ui.film.replays.properties.factories;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.ui.film.replays.properties.UIProperty;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIPoseKeyframeFactory extends UIKeyframeFactory<Pose>
{
    private UIStringList bones;
    private UIPropTransform transform;

    public UIPoseKeyframeFactory(GenericKeyframe<Pose> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.bones = new UIStringList((l) -> this.pickBone(l.get(0)));
        this.bones.background().h(16 * 8);
        this.bones.scroll.cancelScrolling();
        this.transform = new UIPropTransform();
        this.transform.verticalCompact();

        UIProperty property = editor.properties.getProperty(keyframe);
        ModelForm form = (ModelForm) property.property.getForm();
        CubicModel model = form.getModel();

        if (model != null)
        {
            this.bones.add(model.model.getAllGroupKeys());
            this.bones.sort();

            this.bones.setIndex(0);
            this.pickBone(this.bones.getCurrentFirst());
        }

        this.add(this.bones, this.transform);
    }

    private void pickBone(String bone)
    {
        this.transform.setTransform(this.keyframe.value.get(bone));
    }

    public void selectBone(String bone)
    {
        this.bones.setCurrentScroll(bone);
        this.pickBone(bone);
    }
}