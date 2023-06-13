package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.overlay.UINumberOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.world.entities.Entity;

public class InsertTool extends Tool
{
    public InsertTool()
    {
        super(Icons.RIGHTLOAD, Keys.RECORD_TOOL_INSERT);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        UIToggle player = new UIToggle(UIKeys.RECORD_EDITOR_TOOLS_INSERT_COPY, null);
        UINumberOverlayPanel panel = new UINumberOverlayPanel(
            UIKeys.RECORD_EDITOR_TOOLS_INSERT_MODAL_TITLE,
            UIKeys.RECORD_EDITOR_TOOLS_INSERT_MODAL_DESCRIPTION,
            (v) ->
            {
                Range range = context.timeline.calculateRange();
                Record record = context.record;
                int min = range.min < 0 ? context.record.frames.size() - 1 : range.min;
                Frame frame = record.getFrame(min);
                Entity controlled = context.timeline.getContext().menu.bridge.get(IBridgePlayer.class).getController();

                if (frame == null || player.getValue())
                {
                    frame = new Frame();

                    if (player.getValue() && controlled != null)
                    {
                        frame.fromEntity(controlled);
                    }
                }
                int color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1F).getARGBColor();

                for (int i = 0; i < v; i++)
                {
                    int tick = min + i + 1;
                    Frame copy = frame.copy();

                    copy.color = color;

                    if (tick <= record.frames.size())
                    {
                        record.frames.add(tick, copy);
                    }
                    else
                    {
                        record.frames.add(copy);
                    }
                }

                context.timeline.selectFrames(min + 1, min + v.intValue());
                context.timeline.update();
                context.timeline.openCurrentFrame();
            }
        );

        player.h(20);
        panel.bar.prepend(player);
        panel.value.integer().limit(0);
        panel.value.setValue(1);

        UIOverlay.addOverlay(context.timeline.getContext(), panel);

        return true;
    }
}