package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePosition;

public class IdleClip extends Clip
{
    public final ValuePosition position = new ValuePosition("position");

    public IdleClip()
    {
        super();

        this.register(this.position);
    }

    @Override
    public void fromCamera(Camera camera)
    {
        this.position.get().set(camera);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        position.copy(this.position.get());
    }

    @Override
    protected Clip create()
    {
        return new IdleClip();
    }
}