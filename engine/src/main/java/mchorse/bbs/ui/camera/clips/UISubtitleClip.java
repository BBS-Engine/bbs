package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;

public class UISubtitleClip extends UIClip<SubtitleClip>
{
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad size;
    public UITrackpad anchorX;
    public UITrackpad anchorY;
    public UIColor color;
    public UITrackpad windowX;
    public UITrackpad windowY;
    public UIColor background;
    public UITrackpad backgroundOffset;

    public UISubtitleClip(SubtitleClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.x = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.x, new IntType(v.intValue()))));
        this.x.integer();
        this.y = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.y, new IntType(v.intValue()))));
        this.y.integer();

        this.size = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.size, new FloatType(v.floatValue()))));
        this.anchorX = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.anchorX, new FloatType(v.floatValue()))));
        this.anchorY = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.anchorY, new FloatType(v.floatValue()))));
        this.color = new UIColor((c) -> this.editor.postUndo(this.undo(this.clip.color, new IntType(c))));

        this.windowX = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.windowX, new FloatType(v.floatValue()))));
        this.windowY = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.windowY, new FloatType(v.floatValue()))));

        this.background = new UIColor((c) -> this.editor.postUndo(this.undo(this.clip.background, new IntType(c)))).withAlpha();
        this.backgroundOffset = new UITrackpad((v) -> this.editor.postUndo(this.undo(this.clip.backgroundOffset, new FloatType(v.floatValue()))));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(IKey.lazy("Offset")).marginTop(6), UI.row(this.x, this.y));
        this.panels.add(UIClip.label(IKey.lazy("Size")).marginTop(6), this.size, this.color);
        this.panels.add(UIClip.label(IKey.lazy("Anchor")).marginTop(6), UI.row(this.anchorX, this.anchorY));
        this.panels.add(UIClip.label(IKey.lazy("Window")).marginTop(6), UI.row(this.windowX, this.windowY));
        this.panels.add(UIClip.label(IKey.lazy("Background")).marginTop(6), this.background, this.backgroundOffset);
    }

    @Override
    public void fillData()
    {
        super.fillData();
        
        this.x.setValue(this.clip.x.get());
        this.y.setValue(this.clip.y.get());
        this.size.setValue(this.clip.size.get());
        this.anchorX.setValue(this.clip.anchorX.get());
        this.anchorY.setValue(this.clip.anchorY.get());
        this.color.setColor(this.clip.color.get());
        this.windowX.setValue(this.clip.windowX.get());
        this.windowY.setValue(this.clip.windowY.get());
        this.background.setColor(this.clip.background.get());
        this.backgroundOffset.setValue(this.clip.backgroundOffset.get());
    }
}