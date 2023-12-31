package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.ShakeClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;

public class UIShakeClip extends UIClip<ShakeClip>
{
    public UITrackpad shake;
    public UITrackpad shakeAmount;
    public UIBitToggle active;

    public UIShakeClip(ShakeClip modifier, IUIClipsDelegate editor)
    {
        super(modifier, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.shake = new UITrackpad((value) -> this.clip.shake.set(value.floatValue()));
        this.shake.tooltip(UIKeys.CAMERA_PANELS_SHAKE, Direction.BOTTOM);

        this.shakeAmount = new UITrackpad((value) -> this.clip.shakeAmount.set(value.floatValue()));
        this.shakeAmount.tooltip(UIKeys.CAMERA_PANELS_SHAKE_AMOUNT, Direction.BOTTOM);

        this.active = new UIBitToggle((value) -> this.clip.active.set(value)).all();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.C_CLIP.get("bbs:shake")).marginTop(12), UI.row(5, 0, 20, this.shake, this.shakeAmount), this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.shake.setValue(this.clip.shake.get());
        this.shakeAmount.setValue(this.clip.shakeAmount.get());
        this.active.setValue(this.clip.active.get());
    }
}