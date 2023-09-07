package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.TranslateClip;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.modules.UIPointModule;
import mchorse.bbs.ui.film.clips.widgets.UIBitToggle;

public class UITranslateClip extends UIClip<TranslateClip>
{
    public UIPointModule point;
    public UIBitToggle active;

    public UITranslateClip(TranslateClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(this.editor);
        this.active = new UIBitToggle((value) -> this.clip.active.set(value)).point();
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