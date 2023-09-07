package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.Direction;

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
    public UITrackpad shadow;
    public UIPropTransform transform;
    public UITrackpad lineHeight;
    public UITrackpad maxWidth;

    public UISubtitleClip(SubtitleClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.x = new UITrackpad((v) -> this.clip.x.set(v.intValue()));
        this.x.integer();
        this.y = new UITrackpad((v) -> this.clip.y.set(v.intValue()));
        this.y.integer();

        this.size = new UITrackpad((v) -> this.clip.size.set(v.floatValue()));
        this.anchorX = new UITrackpad((v) -> this.clip.anchorX.set(v.floatValue()));
        this.anchorY = new UITrackpad((v) -> this.clip.anchorY.set(v.floatValue()));
        this.color = new UIColor((c) -> this.clip.color.set(c));

        this.windowX = new UITrackpad((v) -> this.clip.windowX.set(v.floatValue()));
        this.windowY = new UITrackpad((v) -> this.clip.windowY.set(v.floatValue()));

        this.background = new UIColor((c) -> this.clip.background.set(c)).withAlpha();
        this.backgroundOffset = new UITrackpad((v) -> this.clip.backgroundOffset.set(v.floatValue()));
        this.shadow = new UITrackpad((v) -> this.clip.shadow.set(v.floatValue())).limit(0);

        this.transform = new UIPropTransform((t) -> this.clip.transform.set(t.copy()));
        this.transform.noLabels();

        this.lineHeight = new UITrackpad((v) -> this.clip.lineHeight.set(v.intValue()));
        this.lineHeight.limit(0).integer().tooltip(IKey.lazy("Line height"), Direction.BOTTOM);
        this.maxWidth = new UITrackpad((v) -> this.clip.maxWidth.set(v.intValue()));
        this.maxWidth.limit(0).integer().tooltip(IKey.lazy("Max width"), Direction.BOTTOM);
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
        this.panels.add(UIClip.label(IKey.lazy("Shadow")).marginTop(6), this.shadow);
        this.panels.add(UIClip.label(IKey.lazy("Transform")).marginTop(6), this.transform);
        this.panels.add(UIClip.label(IKey.lazy("Constraints")).marginTop(6), UI.row(this.lineHeight, this.maxWidth));
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
        this.shadow.setValue(this.clip.shadow.get());
        this.transform.setTransform(this.clip.transform.get());
        this.lineHeight.setValue(this.clip.lineHeight.get());
        this.maxWidth.setValue(this.clip.maxWidth.get());
    }
}