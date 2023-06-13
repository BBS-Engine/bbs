package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.world.entities.Entity;

public class TeleportTool extends Tool
{
    public TeleportTool()
    {
        super(Icons.MOVE_TO, Keys.RECORD_TOOL_TELEPORT);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        int tick = context.timeline.current.tick;

        if (tick >= 0)
        {
            Record record = context.record;
            UIBaseMenu menu = context.timeline.getContext().menu;
            Entity player = menu.bridge.get(IBridgePlayer.class).getController();

            if (record != null)
            {
                if (player != null)
                {
                    record.applyFrame(tick, player);
                }

                Frame frame = record.getFrame(tick);

                if (frame != null && menu instanceof UIDashboard)
                {
                    UIDashboard dashboard = (UIDashboard) menu;

                    dashboard.orbit.position.set(frame.x, frame.y + 1, frame.z);
                    dashboard.orbit.rotation.set(frame.pitch, frame.yaw, 0);
                }
            }
        }

        return true;
    }
}