package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.AngleClip;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIAngleModule;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;

public class UIAngleClip extends UIClip<AngleClip>
{
    public UIAngleModule angle;
    public UIBitToggle active;

    public UIAngleClip(AngleClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.angle = new UIAngleModule(editor).contextMenu();
        this.active = new UIBitToggle((value) -> editor.postUndo(this.undo(this.clip.active, new IntType(value)))).angles();

        this.right.add(this.angle, this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.angle.fill(this.clip.angle);
        this.active.setValue(this.clip.active.get());
    }
}