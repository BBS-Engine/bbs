package mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIProperty;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.utils.pose.UIPoseEditor;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.pose.Pose;
import mchorse.bbs.utils.pose.PoseTransform;

public class UIPoseKeyframeFactory extends UIKeyframeFactory<Pose>
{
    public UIPoseFactoryEditor poseEditor;

    public UIPoseKeyframeFactory(GenericKeyframe<Pose> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.poseEditor = new UIPoseFactoryEditor(keyframe);

        UIProperty property = editor.properties.getProperty(keyframe);
        ModelForm form = (ModelForm) property.property.getForm();
        CubicModel model = form.getModel();

        if (model != null)
        {
            this.poseEditor.setPose(keyframe.getValue(), model.poseGroup);
            this.poseEditor.fillGroups(model.model.getAllGroupKeys());
        }

        this.add(this.poseEditor);
    }

    public static class UIPoseFactoryEditor extends UIPoseEditor
    {
        private GenericKeyframe<Pose> keyframe;

        public UIPoseFactoryEditor(GenericKeyframe<Pose> keyframe)
        {
            super();

            this.keyframe = keyframe;

            ((UIPoseTransforms) this.transform).setKeyframe(keyframe);
        }

        @Override
        protected UIPropTransform createTransformEditor()
        {
            return new UIPoseTransforms().enableHotkeys();
        }

        @Override
        protected void changedPose(Runnable runnable)
        {
            BaseValue.edit(this.keyframe, (kf) -> runnable.run());
        }

        @Override
        protected void setFix(PoseTransform transform, float value)
        {
            this.keyframe.preNotifyParent();
            super.setFix(transform, value);
            this.keyframe.postNotifyParent();
        }
    }

    public static class UIPoseTransforms extends UIPropTransform
    {
        private GenericKeyframe<Pose> keyframe;

        public void setKeyframe(GenericKeyframe<Pose> keyframe)
        {
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

        @Override
        public void setR2(double x, double y, double z)
        {
            this.keyframe.preNotifyParent();
            super.setR2(x, y, z);
            this.keyframe.postNotifyParent();
        }
    }
}