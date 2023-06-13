package mchorse.app.bridge;

import mchorse.app.GameEngine;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.controller.CameraController;

public class BridgeCamera extends BaseBridge implements IBridgeCamera
{
    public BridgeCamera(GameEngine engine)
    {
        super(engine);
    }

    @Override
    public CameraController getCameraController()
    {
        return this.engine.cameraController;
    }
}