package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.camera.UIClips;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.camera.utils.undo.ValueChangeUndo;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.recording.editor.keyframe.UIRecordDopeSheetEditor;
import mchorse.bbs.ui.recording.editor.keyframe.UIRecordGraphEditor;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.undo.IUndo;
import mchorse.bbs.utils.undo.UndoManager;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UIRecordEditor extends UIElement implements IUIClipsDelegate
{
    /* Keyframes */
    public UIElement keyframes;
    public UILabelList<KeyframeChannel> channels;
    public UIKeyframesEditor editor;

    /* Clips */
    public UIElement clips;
    public UIClips timeline;
    public UIClip panel;

    private IUIRecordEditorDelegate delegate;
    private Record record;

    private UndoManager<StructureBase> undoManager;
    private Clip clip;

    public UIRecordEditor(IUIRecordEditorDelegate delegate)
    {
        this.delegate = delegate;

        /* Keyframes */
        this.keyframes = new UIElement();
        this.keyframes.relative(this).wh(1F, 0.5F);

        this.channels = new UILabelList<>(this::selectChannels);
        this.channels.background().multi();

        this.channels.relative(this.keyframes).x(1F, -100).w(100).h(1F);
        this.keyframes.add(this.channels);

        /* Clips */
        this.clips = new UIElement();
        this.clips.relative(this).y(0.5F).wh(1F, 0.5F);

        this.timeline = new UIClips(this, BBS.getFactoryActions());
        this.timeline.relative(this.clips).full();
        this.clips.add(this.timeline);

        this.add(this.keyframes, this.clips);
    }

    private void selectChannels(List<Label<KeyframeChannel>> l)
    {
        if (this.editor != null)
        {
            this.editor.removeFromParent();
            this.editor = null;
        }

        if (l.size() > 1)
        {
            this.editor = new UIRecordDopeSheetEditor(this.delegate);

            List<UISheet> sheets = this.editor.keyframes.getSheets();

            sheets.clear();

            for (Label<KeyframeChannel> e : l)
            {
                sheets.add(new UISheet(e.title.get(), e.title, Colors.ACTIVE, e.value));
            }
        }
        else if (!l.isEmpty())
        {
            UIRecordGraphEditor editor = new UIRecordGraphEditor(this.delegate);

            editor.setChannel(l.get(0).value, Colors.ACTIVE);

            this.editor = editor;
        }

        if (this.editor != null)
        {
            this.editor.relative(this.keyframes).wTo(this.channels.area).h(1F);
            this.add(this.editor);
        }

        this.resize();

        if (this.editor != null)
        {
            this.editor.keyframes.duration = this.record.getLength();
            this.editor.resetView();
        }
    }

    public void setRecord(Record record)
    {
        this.record = record;
        this.undoManager = new UndoManager<>(30);

        this.channels.clear();

        if (this.record != null)
        {
            Map<String, KeyframeChannel> map = record.keyframes.getMap();

            for (String key : map.keySet())
            {
                this.channels.add(IKey.lazy(key), map.get(key));
            }
        }

        this.channels.sort();
        this.channels.setIndex(0);

        this.timeline.setClips(this.record == null ? null : this.record.clips);

        this.selectChannels(this.channels.getCurrent());
    }

    /* IUIClipsDelegate implementation */

    @Override
    public Camera getCamera()
    {
        return this.getContext().menu.bridge.get(IBridgeCamera.class).getCamera();
    }

    @Override
    public Clip getClip()
    {
        return this.clip;
    }

    @Override
    public void pickClip(Clip clip)
    {
        if (this.panel != null)
        {
            if (this.panel.clip == clip)
            {
                this.panel.fillData();

                return;
            }
            else
            {
                this.panel.removeFromParent();
            }
        }

        if (clip == null)
        {
            this.panel = null;
            this.timeline.clearSelection();

            return;
        }

        try
        {
            this.timeline.embedView(null);

            UIClip panel = (UIClip) BBS.getFactoryActions().getData(clip).panelUI.getConstructors()[0].newInstance(clip, this);

            this.panel = panel;
            this.panel.relative(this.editor).w(1F).hTo(this.timeline.area);
            this.editor.addAfter(this.timeline, this.panel);

            this.panel.fillData();
            this.panel.resize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getCursor()
    {
        return this.delegate.getCursor();
    }

    @Override
    public void setCursor(int tick)
    {
        this.delegate.setCursor(tick);
    }

    @Override
    public boolean canUseKeybinds()
    {
        return true;
    }

    @Override
    public void fillData()
    {}

    @Override
    public void embedView(UIElement element)
    {}

    @Override
    public <T extends BaseValue> IUndo createUndo(T property, Consumer<T> consumer)
    {
        BaseType oldValue = property.toData();

        consumer.accept(property);

        BaseType newValue = property.toData();
        ValueChangeUndo undo = new ValueChangeUndo(property.getPath(), oldValue, newValue);

        return undo;
    }

    @Override
    public <T extends BaseValue> IUndo createUndo(T property, BaseType oldValue, BaseType newValue)
    {
        return new ValueChangeUndo(property.getPath(), oldValue, newValue);
    }

    @Override
    public void postUndo(IUndo undo, boolean apply, boolean callback)
    {
        if (undo == null)
        {
            throw new RuntimeException("Given undo is null!");
        }

        UndoManager<StructureBase> undoManager = this.undoManager;

        if (apply)
        {
            undoManager.pushApplyUndo(undo, this.record);
        }
        else
        {
            undoManager.pushUndo(undo);
        }
    }

    @Override
    public void markLastUndoNoMerging()
    {
        if (this.record == null)
        {
            return;
        }

        IUndo<StructureBase> undo = this.undoManager.getCurrentUndo();

        if (undo != null)
        {
            undo.noMerging();
        }
    }

    @Override
    public void updateClipProperty(ValueInt property, int value)
    {}
}