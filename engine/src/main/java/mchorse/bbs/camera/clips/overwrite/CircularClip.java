package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.utils.math.MathUtils;

public class CircularClip extends CameraClip
{
    /**
     * Center point of circular fixture
     */
    public final ValuePoint start = new ValuePoint("start", new Point(0, 0, 0));

    /**
     * Start angle offset (in degrees)
     */
    public final ValueFloat offset = new ValueFloat("offset", 0F);

    /**
     * How much degrees to perform during running
     */
    public final ValueFloat circles = new ValueFloat("circles", 360F);

    /**
     * Distance (in blocks units) from center point
     */
    public final ValueFloat distance = new ValueFloat("distance", 5F);

    /**
     * Pitch of the circular fixture
     */
    public final ValueFloat pitch = new ValueFloat("pitch", 0F);

    /**
     * FOV of the circular fixture
     */
    public final ValueFloat fov = new ValueFloat("fov", 70F);

    public CircularClip()
    {
        super();

        this.register(this.start);
        this.register(this.offset);
        this.register(this.circles);
        this.register(this.distance);
        this.register(this.pitch);
        this.register(this.fov);
    }

    @Override
    public void fromCamera(Camera camera)
    {
        this.start.get().set(camera);
        this.pitch.set(MathUtils.toDeg(camera.rotation.x));
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        int duration = this.duration.get();
        float progress = (context.relativeTick + context.transition) / duration;
        float angle = MathUtils.toRad(this.offset.get() + progress * this.circles.get());

        float distance = this.distance.get();
        double cos = distance * Math.cos(angle);
        double sin = distance * Math.sin(angle);

        Point point = this.start.get();
        double x = point.x + 0.5 + cos;
        double y = point.y;
        double z = point.z + 0.5 + sin;

        float yaw = MathUtils.toDeg((float) Math.atan2(sin, cos)) - 90F;

        position.point.set(x - 0.5, y, z - 0.5);
        position.angle.set(MathUtils.normalizeDegrees(yaw), this.pitch.get(), 0, this.fov.get());
    }

    @Override
    public Clip create()
    {
        return new CircularClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        CircularClip circular = (CircularClip) original;

        float newCircles = circular.circles.get() * (offset / (float) original.duration.get());

        this.offset.set(circular.offset.get() + newCircles);
        this.circles.set(circular.circles.get() - newCircles);
        circular.circles.set(newCircles);
    }
}