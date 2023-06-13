package mchorse.bbs.camera.controller;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.CameraWork;

public class PlayCameraController extends CameraWorkCameraController
{
    public int ticks;

    private int duration;

    public PlayCameraController(IBridge bridge, CameraWork work)
    {
        super(bridge);

        this.setWork(work);

        this.duration = work.calculateDuration();
    }

    @Override
    public void update()
    {
        this.ticks += 1;

        if (this.ticks >= this.duration)
        {
            this.context.shutdown();
            this.bridge.get(IBridgeCamera.class).getCameraController().remove(this);
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        this.apply(camera, this.ticks, transition);
    }
}