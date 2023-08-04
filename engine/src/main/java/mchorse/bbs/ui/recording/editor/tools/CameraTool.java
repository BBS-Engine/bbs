package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.BBSData;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class CameraTool extends Tool
{
    public CameraTool()
    {
        super(Icons.FRUSTUM, Keys.RECORD_TOOL_CAMERA);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        UIDataUtils.openPicker(context.timeline.getContext(), ContentType.CAMERAS, "", (name) ->
        {
            this.generateCameraFrames(context, BBSData.getCameras().load(name));

            List<UIOverlayPanel> children = context.timeline.getContext().menu.getRoot().getChildren(UIOverlayPanel.class);

            if (!children.isEmpty())
            {
                children.get(0).close();
            }
        });

        return true;
    }

    private void generateCameraFrames(ToolContext context, CameraWork camera)
    {
        Position prev = new Position();
        Position position = new Position();
        ClipContext clipContext = new ClipContext();

        int c = camera.clips.calculateDuration();
        int current = context.timeline.calculateRange().min;

        if (current < 0)
        {
            current = 0;
        }

        clipContext.work = camera;
        clipContext.bridge = context.timeline.getContext().menu.bridge;

        for (int i = 0; i <= c; i++)
        {
            Frame frame = new Frame();

            if (i == c)
            {
                camera.apply(clipContext, c - 1, 0.999F, position);
            }
            else
            {
                camera.apply(clipContext, i, 0, position);
            }

            if (i == 0)
            {
                prev.copy(position);
            }

            frame.x = position.point.x;
            frame.y = position.point.y;
            frame.z = position.point.z;
            frame.yaw = position.angle.yaw;
            frame.pitch = position.angle.pitch;
            frame.roll = position.angle.roll;

            context.record.frames.add(current + i, frame);

            prev.copy(position);
        }

        context.timeline.update();
        context.timeline.selectFrames(current, current + c);
    }
}