package mchorse.bbs.game.regions.shapes;

import mchorse.bbs.data.types.MapType;

public class SphereShape extends Shape
{
    public double horizontal = 1;
    public double vertical = 1;

    public SphereShape()
    {}

    public SphereShape(double horizontal, double vertical)
    {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    @Override
    public void copyFrom(Shape shape)
    {
        super.copyFrom(shape);

        if (shape instanceof BoxShape)
        {
            this.horizontal = ((BoxShape) shape).size.x;
            this.vertical = ((BoxShape) shape).size.y;
        }
        else if (shape instanceof SphereShape)
        {
            this.horizontal = ((SphereShape) shape).horizontal;
            this.vertical = ((SphereShape) shape).vertical;
        }
    }

    @Override
    public boolean isInside(double x, double y, double z)
    {
        double dx = x - this.pos.x;
        double dy = y - this.pos.y;
        double dz = z - this.pos.z;

        double rx = dx / this.horizontal;
        double ry = dy / this.vertical;
        double rz = dz / this.horizontal;

        return rx * rx + ry * ry + rz * rz <= 1;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putDouble("horizontal", this.horizontal);
        data.putDouble("vertical", this.vertical);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("horizontal"))
        {
            this.horizontal = data.getDouble("horizontal");
        }

        if (data.has("vertical"))
        {
            this.vertical = data.getDouble("vertical");
        }
    }
}
