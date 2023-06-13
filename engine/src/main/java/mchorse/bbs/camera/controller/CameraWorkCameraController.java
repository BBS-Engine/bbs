package mchorse.bbs.camera.controller;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.core.ITickable;

public abstract class CameraWorkCameraController implements ICameraController, ITickable
{
    protected ClipContext context;
    protected IBridge bridge;
    protected Position position = new Position();

    public CameraWorkCameraController(IBridge bridge)
    {
        this.bridge = bridge;

        this.context = new ClipContext();
        this.context.bridge = bridge;
    }

    public CameraWorkCameraController setWork(CameraWork work)
    {
        this.context.work = work;

        return this;
    }

    public ClipContext getContext()
    {
        return this.context;
    }

    protected void apply(Camera camera, int ticks, float transition)
    {
        this.position.set(camera);
        this.context.work.apply(this.context, ticks, transition, this.position);
        this.position.apply(camera);
    }

    @Override
    public int getPriority()
    {
        return 10;
    }
}