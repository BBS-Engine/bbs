package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

import java.util.List;

public class PasteTool extends Tool
{
    public PasteTool()
    {
        super(Icons.PASTE, Keys.RECORD_TOOL_PASTE);
    }

    @Override
    public boolean canApply(ToolContext context)
    {
        return context.copyPaste != null;
    }

    @Override
    public boolean apply(ToolContext context)
    {
        if (context.copyPaste == null)
        {
            return false;
        }

        UIRecordTimeline timeline = context.timeline;
        BaseType data = context.copyPaste;
        Range range = context.timeline.calculateRange();
        int tick = range.min;

        if (data instanceof MapType)
        {
            Action action = BBS.getFactoryActions().fromData((MapType) context.copyPaste);

            if (action == null)
            {
                return false;
            }

            Frame frame = context.record.getFrame(tick);
            int index = 0;

            if (frame != null)
            {
                frame.actions.add(action);

                index = frame.actions.indexOf(action);
            }

            timeline.recalculateVertical();
            timeline.selectAction(tick, index);
            timeline.openAction(action);
        }
        else
        {
            ListType frames = (ListType) data;

            for (int i = 0; i < frames.size(); i++)
            {
                Frame frame = new Frame();
                MapType map = frames.getMap(i);
                List<Action> actions = null;

                frame.fromData(map);

                context.record.frames.add(tick + i, frame);
            }

            timeline.selectFrames(tick, tick + frames.size() - 1);
            timeline.openCurrentFrame();
            timeline.update();
        }

        return true;
    }
}