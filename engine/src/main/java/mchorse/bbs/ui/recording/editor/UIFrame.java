package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;

public class UIFrame extends UIScrollView
{
    public IRecordEditor editor;

    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;

    public UITrackpad yaw;
    public UITrackpad pitch;
    public UITrackpad bodyYaw;
    public UITrackpad roll;

    public UIToggle isSneaking;
    public UITrackpad fall;
    public UIColor color;

    public UIFrame(IRecordEditor editor)
    {
        this.editor = editor;

        this.x = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.x = v));
        this.y = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.y = v));
        this.z = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.z = v));

        this.yaw = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.yaw = v.floatValue()));
        this.pitch = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.pitch = v.floatValue()));
        this.bodyYaw = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.bodyYaw = v.floatValue()));
        this.roll = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.roll = v.floatValue()));

        this.isSneaking = new UIToggle(UIKeys.RECORD_EDITOR_FRAME_SNEAKING, (b) -> this.editor.editFrame((frame) -> frame.isSneaking = b.getValue()));
        this.fall = new UITrackpad((v) -> this.editor.editFrame((frame) -> frame.fall = v.floatValue()));
        this.color = new UIColor((c) -> this.editor.editFrame((frame) -> frame.color = c));

        this.column().vertical().stretch().scroll().padding(10);

        this.add(UI.label(UIKeys.RECORD_EDITOR_FRAME_POSITION), UI.row(this.x, this.y, this.z));
        this.add(UI.label(UIKeys.RECORD_EDITOR_FRAME_HEAD_ROTATION), UI.row(this.yaw, this.pitch));
        this.add(UI.label(UIKeys.RECORD_EDITOR_FRAME_BODY_YAW), this.bodyYaw);
        this.add(UI.label(UIKeys.RECORD_EDITOR_FRAME_ROLL), this.roll);
        this.add(UI.label(UIKeys.RECORD_EDITOR_FRAME_FALL_DISTANCE), this.fall);
        this.add(this.isSneaking, this.color);
    }

    public void fill(Frame frame)
    {
        this.x.setValue(frame.x);
        this.y.setValue(frame.y);
        this.z.setValue(frame.z);

        this.yaw.setValue(frame.yaw);
        this.pitch.setValue(frame.pitch);
        this.bodyYaw.setValue(frame.bodyYaw);
        this.roll.setValue(frame.roll);

        this.isSneaking.setValue(frame.isSneaking);
        this.fall.setValue(frame.fall);
        this.color.setColor(frame.color);
    }
}