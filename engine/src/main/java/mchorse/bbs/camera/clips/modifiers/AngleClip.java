package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueAngle;

/**
 * Angle modifier
 * 
 * This camera modifier simply adds stored angle values to given 
 * position. 
 */
public class AngleClip extends ComponentClip
{
    public final ValueAngle angle = new ValueAngle("angle", new Angle(0, 0, 0, 0));

    public AngleClip()
    {
        super();

        this.register(this.angle);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        Angle angle = this.angle.get();

        position.angle.yaw = this.isActive(0) ? angle.yaw : position.angle.yaw + angle.yaw;
        position.angle.pitch = this.isActive(1) ? angle.pitch : position.angle.pitch + angle.pitch;
        position.angle.roll = this.isActive(2) ? angle.roll : position.angle.roll + angle.roll;
        position.angle.fov = this.isActive(3) ? angle.fov : position.angle.fov + angle.fov;
    }

    @Override
    public Clip create()
    {
        return new AngleClip();
    }
}