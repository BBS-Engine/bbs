package mchorse.bbs.camera.clips;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.utils.math.Interpolations;

public abstract class CameraClip extends Clip
{
    public void shutdown(ClipContext context)
    {}

    public void fromCamera(Camera camera)
    {}

    public void applyLast(ClipContext context, Position position)
    {
        int duration = this.duration.get();

        this.applyClip(context.setup(this.tick.get() + duration, duration, 0, 0), position);
    }

    public void apply(ClipContext context, Position position)
    {
        if (!this.enabled.get())
        {
            return;
        }

        float factor = this.envelope.factorEnabled(this.duration.get(), context.relativeTick + context.transition);

        if (factor == 1)
        {
            this.applyClip(context, position);
        }
        else
        {
            Position temporary = new Position();

            temporary.set(position);
            this.applyClip(context, temporary);

            position.point.x = Interpolations.lerp(position.point.x, temporary.point.x, factor);
            position.point.y = Interpolations.lerp(position.point.y, temporary.point.y, factor);
            position.point.z = Interpolations.lerp(position.point.z, temporary.point.z, factor);

            position.angle.yaw = Interpolations.lerp(position.angle.yaw, temporary.angle.yaw, factor);
            position.angle.pitch = Interpolations.lerp(position.angle.pitch, temporary.angle.pitch, factor);
            position.angle.roll = Interpolations.lerp(position.angle.roll, temporary.angle.roll, factor);
            position.angle.fov = Interpolations.lerp(position.angle.fov, temporary.angle.fov, factor);
        }
    }

    protected abstract void applyClip(ClipContext context, Position position);
}