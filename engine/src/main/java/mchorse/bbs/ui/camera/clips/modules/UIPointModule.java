package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

/**
 * Point GUI module
 *
 * This class unifies three trackpads into one object which edits a {@link Point},
 * and makes it way easier to reuse in other classes.
 */
public class UIPointModule extends UIAbstractModule
{
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;

    public ValuePoint point;

    public UIPointModule(IUIClipsDelegate editor)
    {
        this(editor, UIKeys.CAMERA_PANELS_POSITION);
    }

    public UIPointModule(IUIClipsDelegate editor, IKey title)
    {
        super(editor);

        this.x = new UITrackpad((value) ->
        {
            this.editor.postUndo(this.editor.createUndo(this.point, (point) -> point.get().x = value));
        });
        this.x.tooltip(UIKeys.X);

        this.y = new UITrackpad((value) ->
        {
            this.editor.postUndo(this.editor.createUndo(this.point, (point) -> point.get().y = value));
        });
        this.y.tooltip(UIKeys.Y);

        this.z = new UITrackpad((value) ->
        {
            this.editor.postUndo(this.editor.createUndo(this.point, (point) -> point.get().z = value));
        });
        this.z.tooltip(UIKeys.Z);

        this.x.values(0.1F);
        this.y.values(0.1F);
        this.z.values(0.1F);

        this.column().vertical().stretch().height(20);
        this.add(UIClip.label(title), this.x, this.y, this.z);
    }

    public UIPointModule contextMenu()
    {
        this.context((menu) -> UICameraUtils.pointContextMenu(menu, this.editor, this.point));

        return this;
    }

    public void fill(ValuePoint point)
    {
        this.point = point;

        this.x.setValue((float) point.get().x);
        this.y.setValue((float) point.get().y);
        this.z.setValue((float) point.get().z);
    }
}