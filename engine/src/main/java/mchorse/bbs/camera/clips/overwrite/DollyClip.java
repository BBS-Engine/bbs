package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.CameraClipContext;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueInterpolation;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3f;

public class DollyClip extends IdleClip
{
    public final ValueFloat distance = new ValueFloat("distance", 0.1F);
    public final ValueInterpolation interp = new ValueInterpolation("interp");
    public final ValueFloat yaw = new ValueFloat("yaw", 0F);
    public final ValueFloat pitch = new ValueFloat("pitch", 0F);

    public DollyClip()
    {
        super();

        this.add(this.distance);
        this.add(this.interp);
        this.add(this.yaw);
        this.add(this.pitch);
    }

    @Override
    public void fromCamera(Camera camera)
    {
        super.fromCamera(camera);

        this.yaw.set(this.position.get().angle.yaw);
        this.pitch.set(this.position.get().angle.pitch);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        super.applyClip(context, position);

        Interpolation interp = this.interp.get();
        Point point = this.position.get().point;
        double x = point.x;
        double y = point.y;
        double z = point.z;

        float yaw = this.yaw.get();
        float pitch = this.pitch.get();
        Vector3f look = Matrices.rotation(MathUtils.toRad(pitch), MathUtils.toRad(180 - yaw)).normalize().mul(this.distance.get());
        float transition = (context.relativeTick + context.transition) / this.duration.get();

        x = interp.interpolate(x, x + look.x, transition);
        y = interp.interpolate(y, y + look.y, transition);
        z = interp.interpolate(z, z + look.z, transition);

        position.point.set(x, y, z);
    }

    @Override
    public Clip create()
    {
        return new DollyClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        DollyClip dolly = (DollyClip) original;
        Position position = new Position();

        dolly.apply(new CameraClipContext().setup(offset, 0), position);

        Point point = dolly.position.get().point;
        double dx = point.x - position.point.x;
        double dy = point.y - position.point.y;
        double dz = point.z - position.point.z;
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        this.position.set(position);
        this.distance.set(dolly.distance.get() - distance);
        dolly.distance.set(distance);
    }
}