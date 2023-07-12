package mchorse.studio.bridge;

import mchorse.studio.StudioEngine;
import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;

public class BridgeVideoRecorder extends BaseBridge implements IBridgeVideoRecorder
{
    public BridgeVideoRecorder(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public VideoRecorder getVideoRecorder()
    {
        return this.engine.video;
    }
}