package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.values.ValueAngle;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

/**
 * Angle GUI module
 *
 * This class unifies four trackpads into one object which edits a {@link Angle},
 * and makes it way easier to reuse in other classes.
 */
public class UIAngleModule extends UIAbstractModule
{
    public UITrackpad yaw;
    public UITrackpad pitch;
    public UITrackpad roll;
    public UITrackpad fov;

    public ValueAngle angle;

    public UIAngleModule(UICameraPanel editor)
    {
        super(editor);

        this.yaw = new UITrackpad((value) ->
        {
            Angle point = this.angle.get().copy();

            point.yaw = value.floatValue();
            this.editor.postUndo(UIClip.undo(this.editor, this.angle, point.toData()));
        });
        this.yaw.tooltip(UIKeys.CAMERA_PANELS_YAW);

        this.pitch = new UITrackpad((value) ->
        {
            Angle point = this.angle.get().copy();

            point.pitch = value.floatValue();
            this.editor.postUndo(UIClip.undo(this.editor, this.angle, point.toData()));
        });
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_PITCH);

        this.roll = new UITrackpad((value) ->
        {
            Angle point = this.angle.get().copy();

            point.roll = value.floatValue();
            this.editor.postUndo(UIClip.undo(this.editor, this.angle, point.toData()));
        });
        this.roll.tooltip(UIKeys.CAMERA_PANELS_ROLL);

        this.fov = new UITrackpad((value) ->
        {
            Angle point = this.angle.get().copy();

            point.fov = value.floatValue();
            this.editor.postUndo(UIClip.undo(this.editor, this.angle, point.toData()));
        });
        this.fov.tooltip(UIKeys.CAMERA_PANELS_FOV);

        this.column().vertical().stretch().height(20);
        this.add(UIClip.label(UIKeys.CAMERA_PANELS_ANGLE), this.yaw, this.pitch, this.roll, this.fov);
    }

    public UIAngleModule contextMenu()
    {
        this.context((menu) -> UICameraUtils.angleContextMenu(menu, this.editor, this.angle));

        return this;
    }

    public void fill(ValueAngle angle)
    {
        this.angle = angle;

        this.yaw.setValue(angle.get().yaw);
        this.pitch.setValue(angle.get().pitch);
        this.roll.setValue(angle.get().roll);
        this.fov.setValue(angle.get().fov);
    }
}