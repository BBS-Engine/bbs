package mchorse.bbs.camera.clips.overwrite;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueInterpolationType;
import mchorse.bbs.camera.values.ValuePositions;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Path camera fixture
 *
 * This fixture is responsible for making smooth camera movements.
 */
public class PathClip extends CameraClip
{
    public static final Vector2d VECTOR = new Vector2d();

    /**
     * List of points in this fixture
     */
    public final ValuePositions points = new ValuePositions("points");

    public final ValueInterpolationType interpolationPoint = new ValueInterpolationType("interpPoint");
    public final ValueInterpolationType interpolationAngle = new ValueInterpolationType("interpAngle");

    public final ValueBoolean circularAutoCenter = new ValueBoolean("circularAutoCenter", true);
    public final ValueDouble circularX = new ValueDouble("circularX", 0D);
    public final ValueDouble circularZ = new ValueDouble("circularZ",0D);

    /* Speed related cache data */
    private float lastTick;
    private Point lastPoint = new Point(0, 0, 0);
    private Point tmpPoint = new Point(0, 0, 0);

    public PathClip()
    {
        super();

        this.register(this.points);
        this.register(this.interpolationPoint);
        this.register(this.interpolationAngle);

        this.register(this.circularAutoCenter);
        this.register(this.circularX);
        this.register(this.circularZ);
    }

    public Position getPoint(int index)
    {
        int size = this.size();

        if (size == 0)
        {
            return new Position(0, 0, 0, 0, 0);
        }

        if (index >= size)
        {
            return this.points.get(size - 1);
        }

        if (index < 0)
        {
            return this.points.get(0);
        }

        return this.points.get(index);
    }

    public int size()
    {
        return this.points.size();
    }

    /**
     * Return the frame for point at the index   
     */
    public int getTickForPoint(int index)
    {
        return (int) ((index / (float) (this.size() - 1)) * this.duration.get());
    }

    @Override
    public void fromCamera(Camera camera)
    {
        this.points.add(new Position(camera));
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        int duration = this.duration.get();

        if (this.points.size() == 0 || duration == 0)
        {
            return;
        }

        int length = this.size() - 1;
        int index;
        float x;

        x = (context.relativeTick + context.transition) / (float) duration;
        x = MathUtils.clamp(x * length, 0, length);
        index = (int) Math.floor(x);
        x = x - index;

        this.applyAngle(position.angle, index, x);
        this.applyPoint(position.point, index, x);
    }

    /**
     * Apply point 
     */
    private void applyPoint(Point point, int index, float progress)
    {
        double x = 0, y = 0, z = 0;

        Position p0 = this.getPoint(index - 1);
        Position p1 = this.getPoint(index);
        Position p2 = this.getPoint(index + 1);
        Position p3 = this.getPoint(index + 2);

        /* Interpolating the position */
        InterpolationType interp = this.interpolationPoint.get();

        if (interp == InterpolationType.CUBIC)
        {
            x = Interpolations.cubic(p0.point.x, p1.point.x, p2.point.x, p3.point.x, progress);
            y = Interpolations.cubic(p0.point.y, p1.point.y, p2.point.y, p3.point.y, progress);
            z = Interpolations.cubic(p0.point.z, p1.point.z, p2.point.z, p3.point.z, progress);
        }
        else if (interp == InterpolationType.HERMITE)
        {
            x = Interpolations.cubicHermite(p0.point.x, p1.point.x, p2.point.x, p3.point.x, progress);
            y = Interpolations.cubicHermite(p0.point.y, p1.point.y, p2.point.y, p3.point.y, progress);
            z = Interpolations.cubicHermite(p0.point.z, p1.point.z, p2.point.z, p3.point.z, progress);
        }
        else if (interp == InterpolationType.CIRCULAR)
        {
            int size = this.size();

            if (index >= size)
            {
                x = p2.point.x;
                y = p2.point.y;
                z = p2.point.z;
            }
            else if (index < 0)
            {
                x = p1.point.x;
                y = p1.point.y;
                z = p1.point.z;
            }
            else
            {
                Vector2d center = this.getCenter();

                double mx = center.x;
                double mz = center.y;

                Vector2d a0 = this.calculateCircular(mx, mz, index - 1);
                Vector2d a1 = this.calculateCircular(mx, mz, index);
                Vector2d a2 = this.calculateCircular(mx, mz, index + 1);
                Vector2d a3 = this.calculateCircular(mx, mz, index + 2);

                double a = Interpolations.cubicHermite(a0.x, a1.x, a2.x, a3.x, progress);
                double d = Interpolations.cubicHermite(a0.y, a1.y, a2.y, a3.y, progress);

                a = a / 180 * Math.PI;

                x = mx + Math.cos(a) * d;
                y = Interpolations.cubicHermite(p0.point.y, p1.point.y, p2.point.y, p3.point.y, progress);
                z = mz + Math.sin(a) * d;
            }
        }
        else if (interp.interp != null)
        {
            Interpolation func = interp.function;

            x = func.interpolate(p1.point.x, p2.point.x, progress);
            y = func.interpolate(p1.point.y, p2.point.y, progress);
            z = func.interpolate(p1.point.z, p2.point.z, progress);
        }

        point.set(x, y, z);
    }

    private Vector2d calculateCircular(double mx, double mz, int index)
    {
        int size = this.size();

        double a = 0;
        double d = 0;
        double lastA = 0;

        if (index < 0)
        {
            index = 0;
        }
        else if (index >= size)
        {
            index = size - 1;
        }

        for (int i = 0; i < size; i++)
        {
            Position p = this.points.get(i);

            double dx = p.point.x - mx;
            double dz = p.point.z - mz;

            d = Math.sqrt(dx * dx + dz * dz);
            a = Math.atan2(dz, dx) / Math.PI * 180;

            if (a < 0)
            {
                a = 360 + a;
            }

            double originalA = a;

            if (Math.abs(a - lastA) > 180)
            {
                a = Interpolations.normalizeYaw(lastA, a);
            }

            if (i == index)
            {
                break;
            }

            lastA = originalA;
        }

        return new Vector2d(a, d);
    }

    public Vector2d getCenter()
    {
        if (this.circularAutoCenter.get())
        {
            this.calculateCenter(VECTOR);
        }
        else
        {
            VECTOR.set(this.circularX.get(), this.circularZ.get());
        }

        return VECTOR;
    }

    public Vector2d calculateCenter(Vector2d vector)
    {
        vector.set(0, 0);

        for (int i = 0; i < this.size(); i++)
        {
            Position position = this.points.get(i);

            vector.x += position.point.x;
            vector.y += position.point.z;
        }

        vector.x /= this.size();
        vector.y /= this.size();

        return vector;
    }

    /**
     * Apply angle  
     */
    private void applyAngle(Angle angle, int index, float progress)
    {
        float yaw, pitch, roll, fov;

        Position p0 = this.getPoint(index - 1);
        Position p1 = this.getPoint(index);
        Position p2 = this.getPoint(index + 1);
        Position p3 = this.getPoint(index + 2);

        /* Interpolating the angle */
        InterpolationType interp = this.interpolationAngle.get();

        if (interp == InterpolationType.CUBIC)
        {
            yaw = Interpolations.cubic(p0.angle.yaw, p1.angle.yaw, p2.angle.yaw, p3.angle.yaw, progress);
            pitch = Interpolations.cubic(p0.angle.pitch, p1.angle.pitch, p2.angle.pitch, p3.angle.pitch, progress);
            roll = Interpolations.cubic(p0.angle.roll, p1.angle.roll, p2.angle.roll, p3.angle.roll, progress);
            fov = Interpolations.cubic(p0.angle.fov, p1.angle.fov, p2.angle.fov, p3.angle.fov, progress);
        }
        else if (interp == InterpolationType.HERMITE)
        {
            yaw = (float) Interpolations.cubicHermite(p0.angle.yaw, p1.angle.yaw, p2.angle.yaw, p3.angle.yaw, progress);
            pitch = (float) Interpolations.cubicHermite(p0.angle.pitch, p1.angle.pitch, p2.angle.pitch, p3.angle.pitch, progress);
            roll = (float) Interpolations.cubicHermite(p0.angle.roll, p1.angle.roll, p2.angle.roll, p3.angle.roll, progress);
            fov = (float) Interpolations.cubicHermite(p0.angle.fov, p1.angle.fov, p2.angle.fov, p3.angle.fov, progress);
        }
        else
        {
            Interpolation func = interp.function == null ? Interpolation.LINEAR : interp.function;

            yaw = func.interpolate(p1.angle.yaw, p2.angle.yaw, progress);
            pitch = func.interpolate(p1.angle.pitch, p2.angle.pitch, progress);
            roll = func.interpolate(p1.angle.roll, p2.angle.roll, progress);
            fov = func.interpolate(p1.angle.fov, p2.angle.fov, progress);
        }

        angle.set(yaw, pitch, roll, fov);
    }

    @Override
    public Clip create()
    {
        return new PathClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        if (this.points.size() < 2)
        {
            return;
        }

        PathClip path = (PathClip) original;
        Position position = new Position();

        path.apply(new ClipContext().setup(offset, 0), position);

        float factor = (offset / (float) original.duration.get()) * (this.size() - 1);
        int originalPoints = (int) Math.ceil(factor);
        int thisPoints = (int) Math.floor(factor);

        List<Position> oP = new ArrayList<Position>();
        List<Position> tP = new ArrayList<Position>();

        for (int i = 0; i < originalPoints; i++)
        {
            oP.add(path.points.get(i).copy());
        }

        oP.add(position.copy());

        for (int i = this.points.size() - 1; i > thisPoints; i--)
        {
            tP.add(this.points.get(i).copy());
        }

        tP.add(position.copy());

        Collections.reverse(tP);

        path.points.set(oP);
        this.points.set(tP);
    }
}