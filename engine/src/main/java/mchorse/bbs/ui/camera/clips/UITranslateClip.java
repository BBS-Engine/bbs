package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.TranslateClip;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;

public class UITranslateClip extends UIClip<TranslateClip>
{
    public UIPointModule point;
    public UIBitToggle active;

    public UITranslateClip(TranslateClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.active = new UIBitToggle((value) -> editor.postUndo(this.undo(this.clip.active, (active) -> active.set(value)))).point();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(this.point.marginTop(12), this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(this.clip.translate);
    }
}