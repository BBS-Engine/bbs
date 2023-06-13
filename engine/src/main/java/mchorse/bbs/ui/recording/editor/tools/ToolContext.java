package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;

public class ToolContext
{
    public Record record;
    public UIRecordTimeline timeline;
    public BaseType copyPaste;

    public ToolContext(UIRecordTimeline timeline)
    {
        this.timeline = timeline;
    }
}