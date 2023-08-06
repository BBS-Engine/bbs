package mchorse.bbs.cubic.data.model;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.utils.Quad;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelUV implements IDataSerializable<ListType>
{
    public Vector2f origin = new Vector2f();
    public Vector2f size = new Vector2f();
    public float rotation;

    public void from(float x1, float y1, float x2, float y2)
    {
        this.origin.x = x1;
        this.origin.y = y1;
        this.size.x = x2 - x1;
        this.size.y = y2 - y1;
    }

    public float sx()
    {
        return this.origin.x;
    }

    public float sy()
    {
        return this.origin.y;
    }

    public float ex()
    {
        return this.origin.x + this.size.x;
    }

    public float ey()
    {
        return this.origin.y + this.size.y;
    }

    public Quad createQuad()
    {
        Quad quad = new Quad();

        quad.p1.set(this.sx(), this.sy(), 0);
        quad.p2.set(this.ex(), this.sy(), 0);
        quad.p3.set(this.ex(), this.ey(), 0);
        quad.p4.set(this.sx(), this.ey(), 0);

        if (this.rotation != 0)
        {
            List<Vector3f> points = new ArrayList<>();

            for (Vector3f p : quad.points)
            {
                points.add(new Vector3f(p));
            }

            for (int i = 0; i < this.rotation / 90; i++)
            {
                points.add(0, points.remove(points.size() - 1));
            }

            for (int i = 0; i < points.size(); i++)
            {
                quad.points.get(i).set(points.get(i));
            }
        }

        return quad;
    }

    @Override
    public ListType toData()
    {
        ListType list = new ListType();

        list.addFloat(this.origin.x);
        list.addFloat(this.origin.y);
        list.addFloat(this.origin.x + this.size.x);
        list.addFloat(this.origin.y + this.size.y);

        if (this.rotation != 0)
        {
            list.addFloat(this.rotation);
        }

        return list;
    }

    @Override
    public void fromData(ListType data)
    {
        float a = data.getFloat(0);
        float b = data.getFloat(1);
        float c = data.getFloat(2);
        float d = data.getFloat(3);

        this.from(a, b, c, d);

        if (data.has(4))
        {
            this.rotation = data.getFloat(4);
        }
    }
}