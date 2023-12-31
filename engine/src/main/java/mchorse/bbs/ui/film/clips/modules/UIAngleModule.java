package mchorse.bbs.ui.film.clips.modules;

import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.values.ValueAngle;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.UIClip;
import mchorse.bbs.ui.film.utils.UICameraUtils;
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

    public UIAngleModule(IUIClipsDelegate editor)
    {
        super(editor);

        this.yaw = new UITrackpad((v) -> BaseValue.edit(this.angle, (value) -> value.get().yaw = v.floatValue()));
        this.yaw.tooltip(UIKeys.CAMERA_PANELS_YAW);

        this.pitch = new UITrackpad((v) -> BaseValue.edit(this.angle, (value) -> value.get().pitch = v.floatValue()));
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_PITCH);

        this.roll = new UITrackpad((v) -> BaseValue.edit(this.angle, (value) -> value.get().roll = v.floatValue()));
        this.roll.tooltip(UIKeys.CAMERA_PANELS_ROLL);

        this.fov = new UITrackpad((v) -> BaseValue.edit(this.angle, (value) -> value.get().fov = v.floatValue()));
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