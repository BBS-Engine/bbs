package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.recording.editor.keyframe.UIRecordDopeSheetEditor;
import mchorse.bbs.ui.recording.editor.keyframe.UIRecordGraphEditor;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.List;
import java.util.Map;

public class UIRecordEditor extends UIElement
{
    public UILabelList<KeyframeChannel> channels;
    public UIKeyframesEditor editor;

    private IUIRecordEditorDelegate delegate;
    private Record record;

    public UIRecordEditor(IUIRecordEditorDelegate delegate)
    {
        this.delegate = delegate;

        this.channels = new UILabelList<>(this::selectChannels);
        this.channels.background().multi();

        this.channels.relative(this).x(1F, -100).w(100).h(1F);

        this.add(this.channels);
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
            this.editor.keyframes.duration = record.size();

            List<UISheet> sheets = this.editor.keyframes.getSheets();
            int i = 0;

            sheets.clear();

            for (Label<KeyframeChannel> e : l)
            {
                sheets.add(new UISheet(e.title.get(), e.title, Colors.ACTIVE, e.value));

                i += 1;
            }

            this.editor.resetView();
        }
        else if (!l.isEmpty())
        {
            UIRecordGraphEditor editor = new UIRecordGraphEditor(this.delegate);

            editor.keyframes.duration = record.size();
            editor.setChannel(l.get(0).value, Colors.ACTIVE);

            this.editor = editor;
        }

        if (this.editor != null)
        {
            this.editor.relative(this).wTo(this.channels.area).h(1F);
            this.add(this.editor);
        }

        this.resize();

        if (this.editor != null)
        {
            this.editor.resetView();
        }
    }

    public void setRecord(Record record)
    {
        this.record = record;

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

        this.selectChannels(this.channels.getCurrent());
    }
}