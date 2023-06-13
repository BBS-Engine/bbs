package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

public class ReverseTool extends Tool
{
    public ReverseTool()
    {
        super(Icons.REVERSE, Keys.RECORD_TOOL_REVERSE);
    }

    @Override
    public boolean canApply(ToolContext context)
    {
        return context.timeline.hasSelectionRange();
    }

    @Override
    public boolean apply(ToolContext context)
    {
        UIRecordTimeline timeline = context.timeline;
        Range range = timeline.calculateRange();
        Record record = context.record;
        int diff = (range.max - range.min) / 2;

        for (int i = range.min; i <= range.min + diff; i++)
        {
            int d = range.max - i + range.min;
            Frame frame = record.frames.get(i);

            record.frames.set(i, record.frames.get(d));
            record.frames.set(d, frame);
        }

        timeline.openCurrentFrame();

        return true;
    }
}