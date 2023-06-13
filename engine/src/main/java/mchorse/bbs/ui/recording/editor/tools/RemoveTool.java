package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;
import mchorse.bbs.utils.colors.Colors;

public class RemoveTool extends Tool
{
    public RemoveTool()
    {
        super(Icons.REMOVE, Keys.RECORD_TOOL_REMOVE);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        if (context.timeline.isActionSelected())
        {
            this.removeAction(context.record, context.timeline);

            return true;
        }
        else if (context.timeline.frame)
        {
            this.removeFrames(context.record, context.timeline);

            return true;
        }

        return false;
    }

    private void removeAction(Record record, UIRecordTimeline timeline)
    {
        int tick = timeline.current.tick;
        int index = timeline.current.index;

        Frame frame = record.getFrame(tick);

        if (frame != null)
        {
            frame.actions.remove(index);
        }

        if (timeline.current.index == 0)
        {
            timeline.current.index = -1;
            timeline.openAction(null);
        }
        else
        {
            timeline.current.index--;
            timeline.openAction(timeline.getAction());
        }

        timeline.recalculateVertical();
    }

    private void removeFrames(Record record, UIRecordTimeline timeline)
    {
        Range range = timeline.calculateRange();

        for (int i = 0; i <= range.calculateOffset(); i++)
        {
            record.frames.remove(range.min);
        }

        timeline.deselect();
        timeline.openCurrentFrame();
        timeline.update();
    }

    @Override
    public void addContext(ToolContext context, ContextMenuManager menu)
    {
        menu.action(this.contextIcon, this.combo.label, Colors.NEGATIVE, () -> this.apply(context));
    }
}