package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.AngleClip;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIAngleModule;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIAngleClip extends UIClip<AngleClip>
{
    public UIAngleModule angle;
    public UIBitToggle active;

    public UIAngleClip(AngleClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.angle = new UIAngleModule(editor).contextMenu();
        this.active = new UIBitToggle((value) -> editor.postUndo(this.undo(this.clip.active, new IntType(value)))).angles();
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView angle = this.createScroll();

        angle.add(this.angle, this.active);

        this.panels.registerPanel(angle, UIKeys.CAMERA_PANELS_ANGLE, Icons.ARC);
        this.panels.setPanel(angle);

        super.registerPanels();
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.angle.fill(this.clip.angle);
        this.active.setValue(this.clip.active.get());
    }
}