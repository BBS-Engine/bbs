package mchorse.bbs.camera.data;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;

public class Position implements IMapSerializable
{
    public static final Position ZERO = new Position();

    public final Point point = new Point(0, 0, 0);
    public final Angle angle = new Angle(0, 0);

    public Position()
    {}

    public Position(Point point, Angle angle)
    {
        this.point.set(point);
        this.angle.set(angle);
    }

    public Position(float x, float y, float z, float yaw, float pitch)
    {
        this.point.set(x, y, z);
        this.angle.set(yaw, pitch);
    }

    public Position(float x, float y, float z, float yaw, float pitch, float roll, float fov)
    {
        this.point.set(x, y, z);
        this.angle.set(yaw, pitch, roll, fov);
    }

    public Position(Camera camera)
    {
        this.set(camera);
    }

    public void set(Position position)
    {
        this.point.set(position.point);
        this.angle.set(position.angle);
    }

    public void set(Camera camera)
    {
        this.point.set(camera);
        this.angle.set(camera);
    }

    public void copy(Position position)
    {
        this.point.set(position.point.x, position.point.y, position.point.z);
        this.angle.set(position.angle.yaw, position.angle.pitch, position.angle.roll, position.angle.fov);
    }

    public void apply(Camera camera)
    {
        camera.position.set(this.point.x, this.point.y, this.point.z);
        camera.rotation.set(MathUtils.toRad(this.angle.pitch), MathUtils.toRad(this.angle.yaw), MathUtils.toRad(this.angle.roll));
        camera.fov = MathUtils.toRad(this.angle.fov);
    }

    public void interpolate(Position position, float factor)
    {
        this.point.x = Interpolations.lerp(this.point.x, position.point.x, factor);
        this.point.y = Interpolations.lerp(this.point.y, position.point.y, factor);
        this.point.z = Interpolations.lerp(this.point.z, position.point.z, factor);
        this.angle.yaw = Interpolations.lerp(this.angle.yaw, position.angle.yaw, factor);
        this.angle.pitch = Interpolations.lerp(this.angle.pitch, position.angle.pitch, factor);
        this.angle.roll = Interpolations.lerp(this.angle.roll, position.angle.roll, factor);
        this.angle.fov = Interpolations.lerp(this.angle.fov, position.angle.fov, factor);
    }

    public Position copy()
    {
        return new Position(this.point.copy(), this.angle.copy());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Position)
        {
            Position position = (Position) obj;

            return this.angle.equals(position.angle) && this.point.equals(position.point);
        }

        return super.equals(obj);
    }

    @Override
    public void toData(MapType data)
    {
        data.put("point", this.point.toData());
        data.put("angle", this.angle.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("point", BaseType.TYPE_MAP) && data.has("angle", BaseType.TYPE_MAP))
        {
            this.point.fromData(data.getMap("point"));
            this.angle.fromData(data.getMap("angle"));
        }
    }
}