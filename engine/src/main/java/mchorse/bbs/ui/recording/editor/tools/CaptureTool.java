package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.world.entities.Entity;

public class CaptureTool extends Tool
{
    public CaptureTool()
    {
        super(Icons.SPHERE, Keys.RECORD_TOOL_CAPTURE);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        int tick = Math.max(context.timeline.current.tick, 0);

        context.timeline.getContext().menu.closeThisMenu();

        Entity player = context.timeline.getContext().menu.bridge.get(IBridgePlayer.class).getController();

        if (player != null)
        {
            BBSData.getRecords().record(context.record.getId(), player, tick);
        }

        return true;
    }
}