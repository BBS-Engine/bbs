package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.DragClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

public class UIDragClip extends UIClip<DragClip>
{
    public UIToggle deterministic;
    public UITrackpad factor;
    public UITrackpad rate;
    public UIBitToggle active;

    public UIDragClip(DragClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.deterministic = new UIToggle(UIKeys.CAMERA_PANELS_DETERMINISTIC, (b) ->
        {
            this.editor.postUndo(this.undo(this.clip.deterministic, (deterministic) -> deterministic.set(b.getValue())));
            this.clip.resetCache();
        });
        this.deterministic.tooltip(UIKeys.CAMERA_PANELS_DETERMINISTIC_TOOLTIP);

        this.factor = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.factor, (factor) -> factor.set(value.floatValue()))));
        this.factor.limit(this.clip.factor).values(0.05F, 0.01F, 0.2F).increment(0.1F).tooltip(UIKeys.CAMERA_PANELS_FACTOR_TOOLTIP);

        this.rate = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.rate, (rate) -> rate.set(value.intValue()))));
        this.rate.limit(this.clip.rate).tooltip(UIKeys.CAMERA_PANELS_RATE_TOOLTIP);

        this.active = new UIBitToggle((value) ->
        {
            this.editor.postUndo(this.undo(this.clip.active, (active) -> active.set(value)));
            this.clip.resetCache();
        }).all();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(IKey.lazy("Drag")).marginTop(12), this.deterministic);
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_FACTOR).marginTop(6), this.factor, this.rate, this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.deterministic.setValue(this.clip.deterministic.get());
        this.factor.setValue(this.clip.factor.get());
        this.rate.setValue(this.clip.rate.get());
        this.active.setValue(this.clip.active.get());
    }
}