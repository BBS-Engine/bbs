package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueKeyframeChannel;

/**
 * Keyframe fixture
 * 
 * This fixture provides a much flexible control over camera, allowing setting 
 * up different transitions between points with different easing.
 */
public class KeyframeClip extends Clip
{
    public final ValueKeyframeChannel x = new ValueKeyframeChannel("x");
    public final ValueKeyframeChannel y = new ValueKeyframeChannel("y");
    public final ValueKeyframeChannel z = new ValueKeyframeChannel("z");
    public final ValueKeyframeChannel yaw = new ValueKeyframeChannel("yaw");
    public final ValueKeyframeChannel pitch = new ValueKeyframeChannel("pitch");
    public final ValueKeyframeChannel roll = new ValueKeyframeChannel("roll");
    public final ValueKeyframeChannel fov = new ValueKeyframeChannel("fov");

    public ValueKeyframeChannel[] channels;

    public KeyframeClip()
    {
        super();

        this.channels = new ValueKeyframeChannel[] {this.x, this.y, this.z, this.yaw, this.pitch, this.roll, this.fov};

        for (ValueKeyframeChannel channel : this.channels)
        {
            this.register(channel);
        }
    }

    @Override
    public void fromCamera(Camera camera)
    {
        Position pos = new Position(camera);

        this.x.get().insert(0, pos.point.x);
        this.y.get().insert(0, pos.point.y);
        this.z.get().insert(0, pos.point.z);
        this.yaw.get().insert(0, pos.angle.yaw);
        this.pitch.get().insert(0, pos.angle.pitch);
        this.roll.get().insert(0, pos.angle.roll);
        this.fov.get().insert(0, pos.angle.fov);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        float t = context.relativeTick + context.transition;

        if (!this.x.get().isEmpty()) position.point.x = this.x.get().interpolate(t);
        if (!this.y.get().isEmpty()) position.point.y = this.y.get().interpolate(t);
        if (!this.z.get().isEmpty()) position.point.z = this.z.get().interpolate(t);
        if (!this.yaw.get().isEmpty()) position.angle.yaw = (float) this.yaw.get().interpolate(t);
        if (!this.pitch.get().isEmpty()) position.angle.pitch = (float) this.pitch.get().interpolate(t);
        if (!this.roll.get().isEmpty()) position.angle.roll = (float) this.roll.get().interpolate(t);
        if (!this.fov.get().isEmpty()) position.angle.fov = (float) this.fov.get().interpolate(t);
    }

    @Override
    public Clip create()
    {
        return new KeyframeClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        for (ValueKeyframeChannel channel : this.channels)
        {
            channel.get().moveX(-offset);
        }
    }
}