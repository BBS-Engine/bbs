package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueAngle;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;

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

        this.add(this.angle);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        Angle angle = this.angle.get();

        position.angle.yaw = this.applyProperty(context.count, 0, position.angle.yaw, angle.yaw);
        position.angle.pitch = this.applyProperty(context.count, 1, position.angle.pitch, angle.pitch);
        position.angle.roll = this.applyProperty(context.count, 2, position.angle.roll, angle.roll);
        position.angle.fov = this.applyProperty(context.count, 3, position.angle.fov, angle.fov);
    }

    private float applyProperty(int count, int i, float absolute, float relative)
    {
        if (this.isActive(i))
        {
            return relative;
        }

        return count == 0 ? absolute : absolute + relative;
    }

    @Override
    public Clip create()
    {
        return new AngleClip();
    }
}