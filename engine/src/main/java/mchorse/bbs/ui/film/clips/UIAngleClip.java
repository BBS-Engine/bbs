package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.AngleClip;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.modules.UIAngleModule;
import mchorse.bbs.ui.film.clips.widgets.UIBitToggle;

public class UIAngleClip extends UIClip<AngleClip>
{
    public UIAngleModule angle;
    public UIBitToggle active;

    public UIAngleClip(AngleClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.angle = new UIAngleModule(this.editor).contextMenu();
        this.active = new UIBitToggle((value) -> this.clip.active.set(value)).angles();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(this.angle.marginTop(12), this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.angle.fill(this.clip.angle);
        this.active.setValue(this.clip.active.get());
    }
}