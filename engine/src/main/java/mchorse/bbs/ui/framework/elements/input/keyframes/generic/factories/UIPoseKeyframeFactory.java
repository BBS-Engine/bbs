package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIProperty;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIPoseKeyframeFactory extends UIKeyframeFactory<Pose>
{
    private UIStringList groups;
    private UIPropTransform transform;

    public UIPoseKeyframeFactory(GenericKeyframe<Pose> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.groups = new UIStringList((l) -> this.pickBone(l.get(0)));
        this.groups.background().h(16 * 8);
        this.groups.scroll.cancelScrolling();
        this.groups.context((menu) ->
        {
            menu.action(Icons.COPY, IKey.lazy("Copy pose"), () ->
            {
                Window.setClipboard(this.keyframe.getValue().toData(), "_ModelCopyPose");
            });

            MapType data = Window.getClipboardMap("_ModelCopyPose");

            if (data != null)
            {
                menu.action(Icons.PASTE, IKey.lazy("Paste pose"), () ->
                {
                    String current = this.groups.getCurrentFirst();

                    BaseValue.edit(this.keyframe, (kf) -> kf.getValue().fromData(data));
                    this.pickBone(current);
                });
            }

            menu.action(Icons.REFRESH, IKey.lazy("Reset pose"), () ->
            {
                String current = this.groups.getCurrentFirst();

                BaseValue.edit(this.keyframe, (kf) -> kf.getValue().transforms.clear());
                this.pickBone(current);
            });
        });
        this.transform = new UIPoseTransforms(this.keyframe).enableHotkeys();
        this.transform.verticalCompact();

        UIProperty property = editor.properties.getProperty(keyframe);
        ModelForm form = (ModelForm) property.property.getForm();
        CubicModel model = form.getModel();

        if (model != null)
        {
            this.groups.add(model.model.getAllGroupKeys());
            this.groups.sort();

            this.groups.setIndex(0);
            this.pickBone(this.groups.getCurrentFirst());
        }

        this.add(this.groups, this.transform);
    }

    private void pickBone(String bone)
    {
        this.transform.setTransform(this.keyframe.getValue().get(bone));
    }

    public void selectBone(String bone)
    {
        this.groups.setCurrentScroll(bone);
        this.pickBone(bone);
    }

    public static class UIPoseTransforms extends UIPropTransform
    {
        private GenericKeyframe<Pose> keyframe;

        public UIPoseTransforms(GenericKeyframe<Pose> keyframe)
        {
            super();

            this.keyframe = keyframe;
        }

        @Override
        public void setT(double x, double y, double z)
        {
            this.keyframe.preNotifyParent();
            super.setT(x, y, z);
            this.keyframe.postNotifyParent();
        }

        @Override
        public void setS(double x, double y, double z)
        {
            this.keyframe.preNotifyParent();
            super.setS(x, y, z);
            this.keyframe.postNotifyParent();
        }

        @Override
        public void setR(double x, double y, double z)
        {
            this.keyframe.preNotifyParent();
            super.setR(x, y, z);
            this.keyframe.postNotifyParent();
        }
    }
}