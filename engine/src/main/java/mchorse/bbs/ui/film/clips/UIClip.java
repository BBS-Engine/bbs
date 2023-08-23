package mchorse.bbs.ui.film.clips;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.widgets.UIEnvelope;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.undo.IUndo;

import java.util.function.Consumer;

public abstract class UIClip <T extends Clip> extends UIElement
{
    public static final IKey CATEGORY = UIKeys.CAMERA_PANELS_KEYS_TITLE;

    public T clip;
    public IUIClipsDelegate editor;

    public UIToggle enabled;
    public UITextbox title;
    public UITrackpad layer;
    public UITrackpad tick;
    public UITrackpad duration;

    public UIEnvelope envelope;

    public UIScrollView panels;

    public static UILabel label(IKey key)
    {
        return UI.label(key).background(() -> BBSSettings.primaryColor(Colors.A50));
    }

    public UIClip(T clip, IUIClipsDelegate editor)
    {
        this.clip = clip;
        this.editor = editor;

        this.enabled = new UIToggle(UIKeys.CAMERA_PANELS_ENABLED, (b) -> this.editor.postUndo(this.undo(this.clip.enabled, (enabled) -> enabled.set(b.getValue()))));
        this.title = new UITextbox(1000, (t) -> this.editor.postUndo(this.undo(this.clip.title, (title) -> title.set(t))));
        this.title.tooltip(UIKeys.CAMERA_PANELS_TITLE_TOOLTIP);
        this.layer = new UITrackpad((v) -> this.editor.updateClipProperty(this.clip.layer, v.intValue()));
        this.layer.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_LAYER);
        this.tick = new UITrackpad((v) -> this.editor.updateClipProperty(this.clip.tick, TimeUtils.fromTime(v)));
        this.tick.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_TICK);
        this.duration = new UITrackpad((v) ->
        {
            this.editor.updateClipProperty(this.clip.duration, TimeUtils.fromTime(v));
            this.updateDuration(TimeUtils.fromTime(v));
        });
        this.duration.limit(1, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_DURATION);
        this.envelope = new UIEnvelope(this);

        this.panels = UI.scrollView(5, 10);
        this.panels.scroll.cancelScrolling();
        this.panels.relative(this).full();

        this.registerUI();
        this.registerPanels();

        this.add(this.panels);
    }

    protected void registerUI()
    {}

    protected void registerPanels()
    {
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_TITLE), this.title);
        this.panels.add(this.enabled.marginBottom(6));
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_METRICS), UI.row(this.layer, this.tick), this.duration);

        this.addEnvelopes();
    }

    protected void addEnvelopes()
    {
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_ENVELOPES_TITLE).marginTop(12), this.envelope);
    }

    public void handleUndo(IUndo<CameraWork> undo, boolean redo)
    {
        this.fillData();
    }

    public <T extends BaseValue> IUndo undo(T property, BaseType newValue)
    {
        return this.editor.createUndo(property, property.toData(), newValue);
    }

    public <T extends BaseValue> IUndo undo(T property, Consumer<T> consumer)
    {
        return this.editor.createUndo(property, consumer);
    }

    protected void updateDuration(int duration)
    {
        this.envelope.updateDuration();
    }

    public void cameraEditorWasOpened()
    {}

    public void editClip(Position position)
    {
        this.fillData();
    }

    public void fillData()
    {
        TimeUtils.configure(this.tick, 0);
        TimeUtils.configure(this.duration, 1);

        this.enabled.setValue(this.clip.enabled.get());
        this.title.setText(this.clip.title.get());
        this.layer.setValue(this.clip.layer.get());
        this.tick.setValue(TimeUtils.toTime(this.clip.tick.get()));
        this.duration.setValue(TimeUtils.toTime(this.clip.duration.get()));
        this.envelope.fillData();
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.gradientHBox(this.area.x - 40, this.area.y, this.area.ex() - 40, this.area.ey(), 0, Colors.A25);
        context.batcher.box(this.area.ex() - 40, this.area.y, this.area.ex(), this.area.ey(), Colors.A25);

        super.render(context);
    }
}