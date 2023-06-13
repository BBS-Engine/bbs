package mchorse.bbs.camera.data;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.math.MathUtils;

public class Angle implements IMapSerializable
{
    public float yaw;
    public float pitch;
    public float roll;
    public float fov = 70;

    public static Angle angle(Point a, Point b)
    {
        return angle(b.x - a.x, b.y - a.y, b.z - a.z);
    }

    public static Angle angle(double dx, double dy, double dz)
    {
        double d = Math.sqrt(dx * dx + dz * dz);
        double yaw = Math.atan2(dz, dx) * 180D / Math.PI + 90D;
        double pitch = -Math.atan2(dy, d) * 180D / Math.PI;

        return new Angle((float) yaw, (float) pitch);
    }

    public Angle(float yaw, float pitch, float roll, float fov)
    {
        this.set(yaw, pitch, roll, fov);
    }

    public Angle(float yaw, float pitch)
    {
        this.set(yaw, pitch);
    }

    public void set(Angle angle)
    {
        this.set(angle.yaw, angle.pitch, angle.roll, angle.fov);
    }

    public void set(float yaw, float pitch, float roll, float fov)
    {
        this.set(yaw, pitch);
        this.roll = roll;
        this.fov = fov;
    }

    public void set(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void set(Camera camera)
    {
        this.set(
            MathUtils.toDeg(camera.rotation.y),
            MathUtils.toDeg(camera.rotation.x),
            MathUtils.toDeg(camera.rotation.z),
            MathUtils.toDeg(camera.fov)
        );
    }

    public Angle copy()
    {
        return new Angle(this.yaw, this.pitch, this.roll, this.fov);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Angle)
        {
            Angle angle = (Angle) obj;

            return this.yaw == angle.yaw && this.pitch == angle.pitch && this.roll == angle.roll && this.fov == angle.fov;
        }

        return super.equals(obj);
    }

    @Override
    public void toData(MapType data)
    {
        data.putFloat("yaw", this.yaw);
        data.putFloat("pitch", this.pitch);
        data.putFloat("roll", this.roll);
        data.putFloat("fov", this.fov);
    }

    @Override
    public void fromData(MapType data)
    {
        this.yaw = data.getFloat("yaw");
        this.pitch = data.getFloat("pitch");
        this.roll = data.getFloat("roll");
        this.fov = data.getFloat("fov");
    }
}