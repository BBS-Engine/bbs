package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBS;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.recording.editor.UIRecordTimeline;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;

public class AddTool extends Tool
{
    public AddTool()
    {
        super(Icons.ADD, Keys.RECORD_TOOL_ADD);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        context.timeline.getContext().replaceContextMenu((menu) ->
        {
            for (Link key : BBS.getFactoryActions().getKeys())
            {
                int color = BBS.getFactoryActions().getData(key).color;
                IKey label = UIKeys.C_ACTION.get(key);

                menu.action(Icons.ADD, label, color, () ->
                {
                    this.createAction(context.record, context.timeline, key);
                });
            }
        });

        return false;
    }

    private void createAction(Record record, UIRecordTimeline timeline, Link key)
    {
        try
        {
            Action action = BBS.getFactoryActions().create(key);
            int tick = timeline.current.tick;
            int index = timeline.current.index;

            Frame frame = record.getFrame(tick);

            if (index >= 0)
            {
                frame.actions.add(index, action);
            }
            else
            {
                frame.actions.add(action);
            }

            timeline.editor.openAction(action);
            timeline.current.index = frame.actions.indexOf(action);
            timeline.recalculateVertical();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}