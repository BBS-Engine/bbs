package mchorse.bbs.camera.controller;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.CameraClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.values.ValueClips;

public abstract class CameraWorkCameraController implements ICameraController, ITickable
{
    protected CameraClipContext context;
    protected IBridge bridge;
    protected Position position = new Position();

    public CameraWorkCameraController(IBridge bridge)
    {
        this.bridge = bridge;

        this.context = new CameraClipContext();
        this.context.bridge = bridge;
    }

    public CameraWorkCameraController setWork(ValueClips clips)
    {
        this.context.clips = clips;

        return this;
    }

    public CameraClipContext getContext()
    {
        return this.context;
    }

    protected void apply(Camera camera, int ticks, float transition)
    {
        this.position.set(camera);

        this.context.clipData.clear();
        this.context.setup(ticks, transition);

        for (Clip clip : this.context.clips.getClips(ticks))
        {
            this.context.apply(clip, this.position);
        }

        this.context.currentLayer = 0;

        this.position.apply(camera);
    }

    @Override
    public int getPriority()
    {
        return 10;
    }
}