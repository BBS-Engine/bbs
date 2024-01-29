package mchorse.bbs.ui.utils.pose;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.pose.Pose;
import mchorse.bbs.utils.pose.PoseTransform;
import mchorse.bbs.utils.pose.Transform;

import java.util.Collection;

public class UIPoseEditor extends UIElement
{
    public UIStringList groups;
    public UITrackpad fix;
    public UIPropTransform transform;

    private String group = "";
    private Pose pose;

    public UIPoseEditor()
    {
        this.groups = new UIStringList((l) -> this.pickBone(l.get(0)));
        this.groups.background().h(UIStringList.DEFAULT_HEIGHT * 8);
        this.groups.scroll.cancelScrolling();
        this.groups.context(() -> new UIPosesContextMenu(this.group, () -> this.pose.toData(), (data) ->
        {
            String current = this.groups.getCurrentFirst();

            this.changedPose(() -> this.pose.fromData(data));
            this.pickBone(current);
        }));
        this.fix = new UITrackpad((v) ->
        {
            Transform t = this.transform.getTransform();

            if (t instanceof PoseTransform)
            {
                PoseTransform poseTransform = (PoseTransform) t;

                this.setFix(poseTransform, v.floatValue());
            }
        });
        this.fix.tooltip(UIKeys.POSE_CONTEXT_FIX_TOOLTIP);
        this.transform = this.createTransformEditor();
        this.transform.verticalCompact();

        this.column().vertical().stretch();
        this.add(this.groups, UI.label(UIKeys.POSE_CONTEXT_FIX), this.fix, this.transform);
    }

    public void setPose(Pose pose, String group)
    {
        this.pose = pose;
        this.group = group;
    }

    public void fillGroups(Collection<String> groups)
    {
        this.groups.clear();
        this.groups.add(groups);
        this.groups.sort();

        this.groups.setIndex(0);
        this.pickBone(this.groups.getCurrentFirst());
    }

    public void selectBone(String bone)
    {
        this.groups.setCurrentScroll(bone);
        this.pickBone(bone);
    }

    /* Subclass overridable methods */

    protected UIPropTransform createTransformEditor()
    {
        return new UIPropTransform().enableHotkeys();
    }

    protected void changedPose(Runnable runnable)
    {
        runnable.run();
    }

    private void pickBone(String bone)
    {
        PoseTransform poseTransform = this.pose.get(bone);

        this.fix.setValue(poseTransform.fix);
        this.transform.setTransform(poseTransform);
    }

    protected void setFix(PoseTransform transform, float value)
    {
        transform.fix = value;
    }
}