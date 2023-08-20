package mchorse.bbs.utils;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform implements IMapSerializable
{
    public static final Transform DEFAULT = new Transform();

    private static final Vector3f DEFAULT_SCALE = new Vector3f(1F, 1F, 1F);

    public final Vector3f translate = new Vector3f();
    public final Vector3f scale = new Vector3f(1F, 1F, 1F);
    public final Vector3f rotate = new Vector3f();

    public void lerp(Transform transform, float a)
    {
        this.translate.lerp(transform.translate, a);
        this.scale.lerp(transform.scale, a);
        this.rotate.lerp(transform.rotate, a);
    }

    public void identity()
    {
        this.translate.set(0, 0, 0);
        this.scale.set(1, 1, 1);
        this.rotate.set(0, 0, 0);
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
        matrix.scale(this.scale);

        return matrix;
    }

    public void apply(MatrixStack stack)
    {
        stack.translate(this.translate);
        stack.rotateZ(this.rotate.z);
        stack.rotateY(this.rotate.y);
        stack.rotateX(this.rotate.x);
        stack.scale(this.scale.x, this.scale.y, this.scale.z);
    }

    public void applyKeyframe(KeyframeChannel channel, String key, float ticks, boolean rads)
    {
        float value = (float) channel.interpolate(ticks);

        if (key.endsWith(".x")) this.translate.x = value;
        else if (key.endsWith(".y")) this.translate.y = value;
        else if (key.endsWith(".z")) this.translate.z = value;
        else if (key.endsWith(".sx")) this.scale.x = value;
        else if (key.endsWith(".sy")) this.scale.y = value;
        else if (key.endsWith(".sz")) this.scale.z = value;
        else if (key.endsWith(".rx")) this.rotate.x = rads ? value / 180F * MathUtils.PI : value;
        else if (key.endsWith(".ry")) this.rotate.y = rads ? value / 180F * MathUtils.PI : value;
        else if (key.endsWith(".rz")) this.rotate.z = rads ? value / 180F * MathUtils.PI : value;
    }

    public boolean fillDefaultValue(ValueDouble value, String key, boolean rads)
    {
        if (key.endsWith(".x"))
        {
            value.set((double) this.translate.x);

            return true;
        }
        else if (key.endsWith(".y"))
        {
            value.set((double) this.translate.y);

            return true;
        }
        else if (key.endsWith(".z"))
        {
            value.set((double) this.translate.z);

            return true;
        }
        else if (key.endsWith(".sx"))
        {
            value.set((double) this.scale.x);

            return true;
        }
        else if (key.endsWith(".sy"))
        {
            value.set((double) this.scale.y);

            return true;
        }
        else if (key.endsWith(".sz"))
        {
            value.set((double) this.scale.z);

            return true;
        }
        else if (key.endsWith(".rx"))
        {
            value.set((double) (rads ? this.rotate.x / MathUtils.PI * 180F : this.rotate.x));

            return true;
        }
        else if (key.endsWith(".ry"))
        {
            value.set((double) (rads ? this.rotate.y / MathUtils.PI * 180F : this.rotate.y));

            return true;
        }
        else if (key.endsWith(".rz"))
        {
            value.set((double) (rads ? this.rotate.z / MathUtils.PI * 180F : this.rotate.z));

            return true;
        }

        return false;
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
                && this.rotate.equals(transform.rotate);
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
    }

    @Override
    public void toData(MapType data)
    {
        data.put("t", DataStorageUtils.vector3fToData(this.translate));
        data.put("s", DataStorageUtils.vector3fToData(this.scale));
        data.put("r", DataStorageUtils.vector3fToData(this.rotate));
    }

    @Override
    public void fromData(MapType data)
    {
        this.identity();

        this.translate.set(DataStorageUtils.vector3fFromData(data.getList("t")));
        this.scale.set(DataStorageUtils.vector3fFromData(data.getList("s"), new Vector3f(DEFAULT_SCALE)));
        this.rotate.set(DataStorageUtils.vector3fFromData(data.getList("r")));
    }

    public boolean isDefault()
    {
        return this.equals(DEFAULT);
    }
}