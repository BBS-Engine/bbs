package mchorse.bbs.utils;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.utils.joml.Matrices;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform implements IMapSerializable
{
    private static final Vector3f DEFAULT_SCALE = new Vector3f(1F, 1F, 1F);

    public static final Transform DEFAULT = new Transform();

    public final Vector3f translate = new Vector3f();
    public final Vector3f scale = new Vector3f(DEFAULT_SCALE);
    public final Vector3f rotate = new Vector3f();
    public final Vector3f rotate2 = new Vector3f();

    public void lerp(Transform transform, float a)
    {
        this.translate.lerp(transform.translate, a);
        this.scale.lerp(transform.scale, a);
        this.rotate.lerp(transform.rotate, a);
        this.rotate2.lerp(transform.rotate2, a);
    }

    public void identity()
    {
        this.translate.set(0, 0, 0);
        this.scale.set(1, 1, 1);
        this.rotate.set(0, 0, 0);
        this.rotate2.set(0, 0, 0);
    }

    public Matrix4f createMatrix()
    {
        return this.setupMatrix(Matrices.TEMP_4F.identity());
    }

    public Matrix4f setupMatrix(Matrix4f matrix)
    {
        matrix.translate(this.translate);
        matrix.rotateZ(this.rotate.z);
        matrix.rotateY(this.rotate.y);
        matrix.rotateX(this.rotate.x);
        matrix.rotateZ(this.rotate2.z);
        matrix.rotateY(this.rotate2.y);
        matrix.rotateX(this.rotate2.x);
        matrix.scale(this.scale);

        return matrix;
    }

    public void apply(MatrixStack stack)
    {
        stack.translate(this.translate);
        stack.rotateZ(this.rotate.z);
        stack.rotateY(this.rotate.y);
        stack.rotateX(this.rotate.x);
        stack.rotateZ(this.rotate2.z);
        stack.rotateY(this.rotate2.y);
        stack.rotateX(this.rotate2.x);
        stack.scale(this.scale.x, this.scale.y, this.scale.z);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof Transform)
        {
            Transform transform = (Transform) obj;

            return this.translate.equals(transform.translate)
                && this.scale.equals(transform.scale)
                && this.rotate.equals(transform.rotate)
                && this.rotate2.equals(transform.rotate2);
        }

        return false;
    }

    public Transform copy()
    {
        Transform transform = new Transform();

        transform.copy(this);

        return transform;
    }

    public void copy(Transform transform)
    {
        this.translate.set(transform.translate);
        this.scale.set(transform.scale);
        this.rotate.set(transform.rotate);
        this.rotate2.set(transform.rotate2);
    }

    @Override
    public void toData(MapType data)
    {
        data.put("t", DataStorageUtils.vector3fToData(this.translate));
        data.put("s", DataStorageUtils.vector3fToData(this.scale));
        data.put("r", DataStorageUtils.vector3fToData(this.rotate));
        data.put("r2", DataStorageUtils.vector3fToData(this.rotate2));
    }

    @Override
    public void fromData(MapType data)
    {
        this.identity();

        this.translate.set(DataStorageUtils.vector3fFromData(data.getList("t")));
        this.scale.set(DataStorageUtils.vector3fFromData(data.getList("s"), DEFAULT_SCALE));
        this.rotate.set(DataStorageUtils.vector3fFromData(data.getList("r")));
        this.rotate2.set(DataStorageUtils.vector3fFromData(data.getList("r2")));
    }

    public boolean isDefault()
    {
        return this.equals(DEFAULT);
    }
}