package mchorse.sandbox.bridge;

import mchorse.sandbox.SandboxEngine;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.controller.CameraController;

public class BridgeCamera extends BaseBridge implements IBridgeCamera
{
    public BridgeCamera(SandboxEngine engine)
    {
        super(engine);
    }

    @Override
    public CameraController getCameraController()
    {
        return this.engine.cameraController;
    }
}