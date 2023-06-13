package mchorse.bbs.utils;

import org.joml.Intersectiond;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class VectorUtils
{
    /* Integer version */

    public static Vector2i resize(float aspect, int w, int h)
    {
        Vector2i resized = new Vector2i(w, h);

        int width = Math.round(aspect * h);

        if (width != w)
        {
            if (width < w)
            {
                resized.x = width;
            }
            else
            {
                resized.y = Math.round(1F / aspect * w);
            }
        }

        return resized;
    }

    public static Vector3i min(Vector3i a, Vector3i b)
    {
        return min(a, b, new Vector3i());
    }

    public static Vector3i min(Vector3i a, Vector3i b, Vector3i result)
    {
        result.x = Math.min(a.x, b.x);
        result.y = Math.min(a.y, b.y);
        result.z = Math.min(a.z, b.z);

        return result;
    }

    public static Vector3i max(Vector3i a, Vector3i b)
    {
        return max(a, b, new Vector3i());
    }

    public static Vector3i max(Vector3i a, Vector3i b, Vector3i result)
    {
        result.x = Math.max(a.x, b.x);
        result.y = Math.max(a.y, b.y);
        result.z = Math.max(a.z, b.z);

        return result;
    }

    /* Float version */

    public static Vector3f min(Vector3f a, Vector3f b)
    {
        return min(a, b, new Vector3f());
    }

    public static Vector3f min(Vector3f a, Vector3f b, Vector3f result)
    {
        result.x = Math.min(a.x, b.x);
        result.y = Math.min(a.y, b.y);
        result.z = Math.min(a.z, b.z);

        return result;
    }

    public static Vector3f max(Vector3f a, Vector3f b)
    {
        return max(a, b, new Vector3f());
    }

    public static Vector3f max(Vector3f a, Vector3f b, Vector3f result)
    {
        result.x = Math.max(a.x, b.x);
        result.y = Math.max(a.y, b.y);
        result.z = Math.max(a.z, b.z);

        return result;
    }

    /* Double version */

    public static Vector3d intersectPlanePerpendicular(Axis axis, Vector3d start, Vector3f direction, Vector3d planeAnchor)
    {
        Axis actualAxis = Axis.Y;

        if (axis == Axis.Y)
        {
            actualAxis = Math.abs(direction.x) > Math.abs(direction.z) ? Axis.X : Axis.Z;
        }

        return intersectPlane(actualAxis, start, direction, planeAnchor);
    }

    public static Vector3d intersectPlane(Axis axis, Vector3d start, Vector3f direction, Vector3d planeAnchor)
    {
        Vector3d end = new Vector3d(direction.x, direction.y, direction.z).mul(128).add(start);

        double a = 0;
        double b = 0;
        double c = 0;
        double d = 0;

        if (axis == Axis.X) a = 1;
        else if (axis == Axis.Y) b = 1;
        else if (axis == Axis.Z) c = 1;

        if (a != 0) d = planeAnchor.x;
        else if (b != 0) d = planeAnchor.y;
        else if (c != 0) d = planeAnchor.z;

        Vector3d intersection = new Vector3d();

        if (Intersectiond.intersectLineSegmentPlane(start.x, start.y, start.z, end.x, end.y, end.z, a, b, c, -d, intersection))
        {
            return intersection;
        }

        return null;
    }

    public static Vector3d min(Vector3d a, Vector3d b)
    {
        return min(a, b, new Vector3d());
    }

    public static Vector3d min(Vector3d a, Vector3d b, Vector3d result)
    {
        result.x = Math.min(a.x, b.x);
        result.y = Math.min(a.y, b.y);
        result.z = Math.min(a.z, b.z);

        return result;
    }

    public static Vector3d max(Vector3d a, Vector3d b)
    {
        return max(a, b, new Vector3d());
    }

    public static Vector3d max(Vector3d a, Vector3d b, Vector3d result)
    {
        result.x = Math.max(a.x, b.x);
        result.y = Math.max(a.y, b.y);
        result.z = Math.max(a.z, b.z);

        return result;
    }
}