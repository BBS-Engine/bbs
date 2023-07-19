package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

public class UISubtitleClip extends UIClip<SubtitleClip>
{
    public UITextbox label;
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

        this.label = new UITextbox(1000, (t) -> this.editor.postUndo(this.undo(this.clip.label, new StringType(t))));
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
        UIScrollView subtitle = this.createScroll();

        subtitle.add(UI.label(IKey.lazy("Label")).background(), this.label);
        subtitle.add(UI.label(IKey.lazy("Offset")).background(), UI.row(this.x, this.y));
        subtitle.add(UI.label(IKey.lazy("Size")).background(), this.size, this.color);
        subtitle.add(UI.label(IKey.lazy("Anchor")).background(), UI.row(this.anchorX, this.anchorY));
        subtitle.add(UI.label(IKey.lazy("Window")).background(), UI.row(this.windowX, this.windowY));
        subtitle.add(UI.label(IKey.lazy("Background")).background(), this.background, this.backgroundOffset);

        this.panels.registerPanel(subtitle, IKey.lazy("Subtitle"), Icons.FONT);
        this.panels.setPanel(subtitle);

        super.registerPanels();
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.label.setText(this.clip.label.get());
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