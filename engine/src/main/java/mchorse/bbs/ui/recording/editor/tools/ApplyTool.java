package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBSData;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.recording.editor.utils.UIApplyToolOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

import java.util.List;

public class ApplyTool extends Tool
{
    public ApplyTool()
    {
        super(Icons.IN, Keys.RECORD_TOOL_APPLY);
    }

    @Override
    public boolean canApply(ToolContext context)
    {
        return context.timeline.hasSelectionRange();
    }

    @Override
    public boolean apply(ToolContext context)
    {
        UIDataUtils.openPicker(context.timeline.getContext(), ContentType.RECORDS, "", (name) ->
        {
            Record record = BBSData.getRecords().load(name);
            UIApplyToolOverlayPanel panel = new UIApplyToolOverlayPanel(
                UIKeys.RECORD_EDITOR_TOOLS_APPLY_PROPERTIES,
                UIKeys.RECORD_EDITOR_TOOLS_APPLY_DESCRIPTION,
                (data) ->
                {
                    this.applyRecord(context, data.properties, data.relative, record);
                }
            );

            UIOverlay.addOverlay(context.timeline.getContext(), panel);

            List<UIOverlayPanel> children = context.timeline.getContext().menu.getRoot().getChildren(UIOverlayPanel.class);

            if (!children.isEmpty())
            {
                children.get(0).close();
            }
        });

        return true;
    }

    private void applyRecord(ToolContext context, List<String> properties, boolean relative, Record other)
    {
        if (properties.isEmpty())
        {
            return;
        }

        Range range = context.timeline.calculateRange();

        if (range.max >= other.frames.size())
        {
            return;
        }

        for (int i = range.min; i <= range.max; i++)
        {
            Frame frame = context.record.frames.get(i);
            Frame otherFrame = other.frames.get(i);

            for (String property : properties)
            {
                double value = Frame.get(property, frame);
                double otherValue = Frame.get(property, otherFrame);

                if (relative)
                {
                    double otherFirst = Frame.get(property, other.frames.get(range.min));

                    Frame.set(property, frame, value + (otherValue - otherFirst));
                }
                else
                {
                    Frame.set(property, frame, otherValue);
                }
            }
        }
    }
}
