package mchorse.bbs.data;

import mchorse.bbs.data.types.ListType;
import org.joml.Matrix3f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DataStorageUtils
{
    /* Vector2i */

    public static ListType vector2iToData(Vector2i vector)
    {
        ListType list = new ListType();

        list.addInt(vector.x);
        list.addInt(vector.y);

        return list;
    }

    public static Vector2i vector2iFromData(ListType element)
    {
        return vector2iFromData(element, new Vector2i());
    }

    public static Vector2i vector2iFromData(ListType element, Vector2i defaultValue)
    {
        if (element != null && element.size() >= 2)
        {
            return new Vector2i(element.getInt(0), element.getInt(1));
        }

        return defaultValue;
    }

    /* Vector3f */

    public static ListType vector3fToData(Vector3f vector)
    {
        ListType list = new ListType();

        list.addFloat(vector.x);
        list.addFloat(vector.y);
        list.addFloat(vector.z);

        return list;
    }

    public static Vector3f vector3fFromData(ListType element)
    {
        return vector3fFromData(element, new Vector3f());
    }

    public static Vector3f vector3fFromData(ListType element, Vector3f defaultValue)
    {
        if (element != null && element.size() >= 3)
        {
            return new Vector3f(element.getFloat(0), element.getFloat(1), element.getFloat(2));
        }

        return defaultValue;
    }

    /* Vector3d */

    public static ListType vector3dToData(Vector3d vector)
    {
        ListType list = new ListType();

        list.addDouble(vector.x);
        list.addDouble(vector.y);
        list.addDouble(vector.z);

        return list;
    }

    public static Vector3d vector3dFromData(ListType element)
    {
        return vector3dFromData(element, new Vector3d());
    }

    public static Vector3d vector3dFromData(ListType element, Vector3d defaultValue)
    {
        if (element != null && element.size() >= 3)
        {
            return new Vector3d(element.getDouble(0), element.getDouble(1), element.getDouble(2));
        }

        return defaultValue;
    }

    /* Vector4f */

    public static ListType vector4fToData(Vector4f vector)
    {
        ListType list = new ListType();

        list.addFloat(vector.x);
        list.addFloat(vector.y);
        list.addFloat(vector.z);
        list.addFloat(vector.w);

        return list;
    }

    public static Vector4f vector4fFromData(ListType element)
    {
        return vector4fFromData(element, new Vector4f());
    }

    public static Vector4f vector4fFromData(ListType element, Vector4f defaultValue)
    {
        if (element != null && element.size() >= 4)
        {
            return new Vector4f(element.getFloat(0), element.getFloat(1), element.getFloat(2), element.getFloat(3));
        }

        return defaultValue;
    }

    /* Matrix3f */

    public static ListType matrix3fToData(Matrix3f matrix)
    {
        ListType list = new ListType();

        list.addFloat(matrix.m00);
        list.addFloat(matrix.m01);
        list.addFloat(matrix.m02);
        list.addFloat(matrix.m10);
        list.addFloat(matrix.m11);
        list.addFloat(matrix.m12);
        list.addFloat(matrix.m20);
        list.addFloat(matrix.m21);
        list.addFloat(matrix.m22);

        return list;
    }

    public static Matrix3f matrix3fFromData(ListType element)
    {
        return matrix3fFromData(element, new Matrix3f());
    }

    public static Matrix3f matrix3fFromData(ListType element, Matrix3f defaultValue)
    {
        if (element != null && element.size() >= 9)
        {
            return new Matrix3f(
                element.getFloat(0), element.getFloat(1), element.getFloat(2),
                element.getFloat(3), element.getFloat(4), element.getFloat(5),
                element.getFloat(6), element.getFloat(7), element.getFloat(8)
            );
        }

        return defaultValue;
    }
}