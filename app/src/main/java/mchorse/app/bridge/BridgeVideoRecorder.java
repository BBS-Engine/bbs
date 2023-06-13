package mchorse.app.bridge;

import mchorse.app.GameEngine;
import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;

public class BridgeVideoRecorder extends BaseBridge implements IBridgeVideoRecorder
{
    public BridgeVideoRecorder(GameEngine engine)
    {
        super(engine);
    }

    @Override
    public VideoRecorder getVideoRecorder()
    {
        return this.engine.video;
    }
}