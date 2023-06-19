package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.overwrite.IdleClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIAngleModule;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.utils.UICameraUtils;

/**
 * Idle clip panel
 *
 * This panel is responsible for editing an idle clip. This panel uses basic
 * point and angle modules for manipulating idle clip's position.
 */
public class UIIdleClip extends UIClip<IdleClip>
{
    public UIPointModule point;
    public UIAngleModule angle;

    public UIIdleClip(IdleClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.point = new UIPointModule(editor);
        this.angle = new UIAngleModule(editor);

        this.left.add(this.point, this.angle);
        this.left.context((menu) -> UICameraUtils.positionContextMenu(menu, editor, clip.position));
    }

    @Override
    public void editClip(Position position)
    {
        this.editor.postUndo(this.undo(this.clip.position, position.toData()));

        super.editClip(position);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(clip.position.getPoint());
        this.angle.fill(clip.position.getAngle());
    }
}