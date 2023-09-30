package mchorse.studio.bridge;

import mchorse.bbs.bridge.IBridgeVideoScreenshot;
import mchorse.bbs.utils.recording.ScreenshotRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;
import mchorse.studio.StudioEngine;

public class BridgeVideoRecorder extends BaseBridge implements IBridgeVideoScreenshot
{
    public BridgeVideoRecorder(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public ScreenshotRecorder getScreenshotRecorder()
    {
        return this.engine.screenshot;
    }

    @Override
    public VideoRecorder getVideoRecorder()
    {
        return this.engine.video;
    }
}