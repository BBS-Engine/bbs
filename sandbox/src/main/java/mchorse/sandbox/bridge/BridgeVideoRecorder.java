package mchorse.sandbox.bridge;

import mchorse.sandbox.SandboxEngine;
import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;

public class BridgeVideoRecorder extends BaseBridge implements IBridgeVideoRecorder
{
    public BridgeVideoRecorder(SandboxEngine engine)
    {
        super(engine);
    }

    @Override
    public VideoRecorder getVideoRecorder()
    {
        return this.engine.video;
    }
}