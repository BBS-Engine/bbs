package mchorse.bbs.utils;

import mchorse.bbs.utils.joml.Vectors;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;

public class Quad
{
    public Vector3f p1 = new Vector3f();
    public Vector3f p2 = new Vector3f();
    public Vector3f p3 = new Vector3f();
    public Vector3f p4 = new Vector3f();

    public final List<Vector3f> points = Arrays.asList(this.p1, this.p2, this.p3, this.p4);

    public void copy(Quad quad)
    {
        for (int i = 0; i < this.points.size(); i++)
        {
            this.points.get(i).set(quad.points.get(i));
        }
    }

    public void transform(Matrix4f matrix)
    {
        Vector4f vector = Vectors.TEMP_4F;

        for (Vector3f p : this.points)
        {
            vector.set(p.x, p.y, p.z, 1);
            matrix.transform(vector);

            p.set(vector.x, vector.y, vector.z);
        }
    }
}