package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

/**
 * Keyframe fixture
 * 
 * This fixture provides a much flexible control over camera, allowing setting 
 * up different transitions between points with different easing.
 */
public class KeyframeClip extends CameraClip
{
    public final KeyframeChannel x = new KeyframeChannel("x");
    public final KeyframeChannel y = new KeyframeChannel("y");
    public final KeyframeChannel z = new KeyframeChannel("z");
    public final KeyframeChannel yaw = new KeyframeChannel("yaw");
    public final KeyframeChannel pitch = new KeyframeChannel("pitch");
    public final KeyframeChannel roll = new KeyframeChannel("roll");
    public final KeyframeChannel fov = new KeyframeChannel("fov");

    public KeyframeChannel[] channels;

    public KeyframeClip()
    {
        super();

        this.channels = new KeyframeChannel[] {this.x, this.y, this.z, this.yaw, this.pitch, this.roll, this.fov};

        for (KeyframeChannel channel : this.channels)
        {
            this.add(channel);
        }
    }

    @Override
    public void fromCamera(Camera camera)
    {
        Position pos = new Position(camera);

        this.x.insert(0, pos.point.x);
        this.y.insert(0, pos.point.y);
        this.z.insert(0, pos.point.z);
        this.yaw.insert(0, pos.angle.yaw);
        this.pitch.insert(0, pos.angle.pitch);
        this.roll.insert(0, pos.angle.roll);
        this.fov.insert(0, pos.angle.fov);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        float t = context.relativeTick + context.transition;

        if (!this.x.isEmpty()) position.point.x = this.x.interpolate(t);
        if (!this.y.isEmpty()) position.point.y = this.y.interpolate(t);
        if (!this.z.isEmpty()) position.point.z = this.z.interpolate(t);
        if (!this.yaw.isEmpty()) position.angle.yaw = (float) this.yaw.interpolate(t);
        if (!this.pitch.isEmpty()) position.angle.pitch = (float) this.pitch.interpolate(t);
        if (!this.roll.isEmpty()) position.angle.roll = (float) this.roll.interpolate(t);
        if (!this.fov.isEmpty()) position.angle.fov = (float) this.fov.interpolate(t);
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

        for (KeyframeChannel channel : this.channels)
        {
            channel.moveX(-offset);
        }
    }
}