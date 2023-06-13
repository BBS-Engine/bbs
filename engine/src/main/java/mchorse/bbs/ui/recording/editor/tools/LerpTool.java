package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

public class LerpTool extends Tool
{
    public LerpTool()
    {
        super(Icons.GRAPH, Keys.RECORD_TOOL_LERP);
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
        Frame first = record.frames.get(range.min);
        Frame last = record.frames.get(range.max);

        for (int i = range.min + 1; i < range.max; i++)
        {
            Frame frame = record.frames.get(i);

            frame.lerp(first, last, (i - range.min) / (float) (range.max - range.min));
        }

        timeline.openCurrentFrame();

        return true;
    }
}