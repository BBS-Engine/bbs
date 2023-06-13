package mchorse.bbs.camera.data;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class Point implements IMapSerializable
{
    public double x;
    public double y;
    public double z;

    public Point(double x, double y, double z)
    {
        this.set(x, y, z);
    }

    public Point(Camera camera)
    {
        this.set(camera);
    }

    public void set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Point point)
    {
        this.set(point.x, point.y, point.z);
    }

    public void set(Camera camera)
    {
        this.set(camera.position.x, camera.position.y, camera.position.z);
    }

    public Point copy()
    {
        return new Point(this.x, this.y, this.z);
    }

    public double length(Point point)
    {
        double dx = point.x - this.x;
        double dy = point.y - this.y;
        double dz = point.z - this.z;

        return Math.sqrt(dx * dx  + dy * dy + dz * dz);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Point)
        {
            Point point = (Point) obj;

            return this.x == point.x && this.y == point.y && this.z == point.z;
        }

        return super.equals(obj);
    }

    @Override
    public void toData(MapType data)
    {
        data.putDouble("x", this.x);
        data.putDouble("y", this.y);
        data.putDouble("z", this.z);
    }

    @Override
    public void fromData(MapType data)
    {
        this.x = data.getDouble("x");
        this.y = data.getDouble("y");
        this.z = data.getDouble("z");
    }
}