package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.overwrite.IdleClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIAngleModule;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.icons.Icons;

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
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.angle = new UIAngleModule(editor);
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView idle = this.createScroll();

        idle.add(this.point, this.angle);
        idle.context((menu) -> UICameraUtils.positionContextMenu(menu, editor, clip.position));

        this.panels.registerPanel(idle, UIKeys.CAMERA_PANELS_POSITION, Icons.FRUSTUM);
        this.panels.setPanel(idle);

        super.registerPanels();
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