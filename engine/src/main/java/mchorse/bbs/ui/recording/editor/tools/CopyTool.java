package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

public class CopyTool extends Tool
{
    public CopyTool()
    {
        super(Icons.COPY, Keys.RECORD_TOOL_COPY);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        Action current = context.timeline.getAction();

        if (context.timeline.frame && context.timeline.hasSelectionRange())
        {
            Range range = context.timeline.calculateRange();
            ListType list = new ListType();

            for (int i = range.min; i <= range.max; i++)
            {
                list.add(context.record.frames.get(i).toData());
            }

            context.copyPaste = list;

            return true;
        }
        else if (current != null)
        {
            context.copyPaste = BBS.getFactoryActions().toData(current);

            return true;
        }

        return false;
    }
}