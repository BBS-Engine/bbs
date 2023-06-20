package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.DragClip;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIDragClip extends UIClip<DragClip>
{
    public UIToggle deterministic;
    public UITrackpad factor;
    public UITrackpad rate;
    public UIBitToggle active;

    public UIDragClip(DragClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.deterministic = new UIToggle(UIKeys.CAMERA_PANELS_DETERMINISTIC, (b) ->
        {
            this.editor.postUndo(this.undo(this.clip.deterministic, new ByteType(b.getValue())));
            this.clip.resetCache();
        });
        this.deterministic.tooltip(UIKeys.CAMERA_PANELS_DETERMINISTIC_TOOLTIP);

        this.factor = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.factor, new FloatType(value.floatValue()))));
        this.factor.limit(this.clip.factor).values(0.05F, 0.01F, 0.2F).increment(0.1F).tooltip(UIKeys.CAMERA_PANELS_FACTOR_TOOLTIP);

        this.rate = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.rate, new IntType(value.intValue()))));
        this.rate.limit(this.clip.rate).tooltip(UIKeys.CAMERA_PANELS_RATE_TOOLTIP);

        this.active = new UIBitToggle((value) ->
        {
            this.editor.postUndo(this.undo(this.clip.active, new IntType(value)));
            this.clip.resetCache();
        }).all();
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView drag = this.createScroll();

        drag.add(this.deterministic, UI.label(UIKeys.CAMERA_PANELS_FACTOR).background(), this.factor, this.rate, this.active);

        this.panels.registerPanel(drag, UIKeys.CAMERA_PANELS_DRAG, Icons.FADING);
        this.panels.setPanel(drag);

        super.registerPanels();
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