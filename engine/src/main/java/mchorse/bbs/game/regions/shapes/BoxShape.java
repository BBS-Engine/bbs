package mchorse.bbs.game.regions.shapes;

import mchorse.bbs.data.types.MapType;
import org.joml.Vector3d;

public class BoxShape extends Shape
{
    public Vector3d size = new Vector3d(1, 1, 1);

    @Override
    public void copyFrom(Shape shape)
    {
        super.copyFrom(shape);

        if (shape instanceof BoxShape)
        {
            this.size.set(((BoxShape) shape).size);
        }
        else if (shape instanceof SphereShape)
        {
            double h = ((SphereShape) shape).horizontal;
            double v = ((SphereShape) shape).vertical;

            this.size.set(h, v, h);
        }
    }

    @Override
    public boolean isInside(double x, double y, double z)
    {
        double dx = x - this.pos.x;
        double dy = y - this.pos.y;
        double dz = z - this.pos.z;

        return Math.abs(dx) < this.size.x && Math.abs(dy) < this.size.y && Math.abs(dz) < this.size.z;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putDouble("sX", this.size.x);
        data.putDouble("sY", this.size.y);
        data.putDouble("sZ", this.size.z);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.size.x = data.getDouble("sX");
        this.size.y = data.getDouble("sY");
        this.size.z = data.getDouble("sZ");
    }
}