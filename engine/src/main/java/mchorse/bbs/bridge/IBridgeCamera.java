package mchorse.bbs.bridge;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.CameraController;

public interface IBridgeCamera
{
    public default Camera getCamera()
    {
        return this.getCameraController().camera;
    }

    public CameraController getCameraController();
}