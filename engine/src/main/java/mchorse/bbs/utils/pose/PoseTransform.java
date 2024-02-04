package mchorse.bbs.utils.pose;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.math.Interpolations;

public class PoseTransform extends Transform
{
    private static PoseTransform DEFAULT = new PoseTransform();

    public float fix;

    @Override
    public void identity()
    {
        super.identity();

        this.fix = 0F;
    }

    @Override
    public void lerp(Transform transform, float a)
    {
        if (transform instanceof PoseTransform)
        {
            this.fix = Interpolations.lerp(this.fix, ((PoseTransform) transform).fix, a);
        }

        super.lerp(transform, a);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof PoseTransform)
        {
            result = result && this.fix == ((PoseTransform) obj).fix;
        }

        return result;
    }

    @Override
    public Transform copy()
    {
        PoseTransform transform = new PoseTransform();

        transform.copy(this);

        return transform;
    }

    @Override
    public void copy(Transform transform)
    {
        if (transform instanceof PoseTransform)
        {
            this.fix = ((PoseTransform) transform).fix;
        }

        super.copy(transform);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putFloat("fix", this.fix);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.fix = data.getFloat("fix");
    }

    @Override
    public boolean isDefault()
    {
        return this.equals(DEFAULT);
    }
}