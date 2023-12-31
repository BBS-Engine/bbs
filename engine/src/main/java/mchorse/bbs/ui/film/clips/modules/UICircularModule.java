package mchorse.bbs.ui.film.clips.modules;

import mchorse.bbs.camera.clips.overwrite.CircularClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.UIClip;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

/**
 * Circular GUI module
 *
 * This class unifies four trackpads into one object which edits
 * {@link CircularClip}'s other properties, and makes it way easier to reuse
 * in other classes.
 */
public class UICircularModule extends UIAbstractModule
{
    public UITrackpad offset;
    public UITrackpad circles;
    public UITrackpad distance;
    public UITrackpad pitch;
    public UITrackpad fov;

    public CircularClip clip;

    public UICircularModule(IUIClipsDelegate editor)
    {
        super(editor);

        this.offset = new UITrackpad((value) -> this.clip.offset.set(value.floatValue()));
        this.offset.tooltip(UIKeys.CAMERA_PANELS_OFFSET);

        this.circles = new UITrackpad((value) -> this.clip.circles.set(value.floatValue()));
        this.circles.tooltip(UIKeys.CAMERA_PANELS_CIRCLES);

        this.distance = new UITrackpad((value) -> this.clip.distance.set(value.floatValue()));
        this.distance.tooltip(UIKeys.CAMERA_PANELS_DISTANCE);

        this.pitch = new UITrackpad((value) -> this.clip.pitch.set(value.floatValue()));
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_PITCH);

        this.fov = new UITrackpad((value) -> this.clip.fov.set(value.floatValue()));
        this.fov.tooltip(UIKeys.CAMERA_PANELS_FOV);

        this.column().vertical().stretch().height(20);
        this.add(UIClip.label(UIKeys.CAMERA_PANELS_CIRCLE), this.offset, this.circles, this.distance, this.pitch, this.fov);
    }

    public void fill(CircularClip clip)
    {
        this.clip = clip;

        this.offset.setValue(clip.offset.get());
        this.circles.setValue(clip.circles.get());
        this.distance.setValue(clip.distance.get());
        this.pitch.setValue(clip.pitch.get());
        this.fov.setValue(clip.fov.get());
    }
}