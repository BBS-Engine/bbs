package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.ui.framework.elements.input.UITransformations;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.math.MathUtils;

public class UIPropTransforms extends UITransformations
{
    private Transform transform;

    public void setTransform(Transform transform)
    {
        this.transform = transform;

        this.fillT(transform.translate.x, transform.translate.y, transform.translate.z);
        this.fillS(transform.scale.x, transform.scale.y, transform.scale.z);
        this.fillR(MathUtils.toDeg(transform.rotate.x), MathUtils.toDeg(transform.rotate.y), MathUtils.toDeg(transform.rotate.z));
    }

    @Override
    public void setT(double x, double y, double z)
    {
        this.transform.translate.set((float) x, (float) y, (float) z);
    }

    @Override
    public void setS(double x, double y, double z)
    {
        this.transform.scale.set((float) x, (float) y, (float) z);
    }

    @Override
    public void setR(double x, double y, double z)
    {
        this.transform.rotate.set(MathUtils.toRad((float) x), MathUtils.toRad((float) y), MathUtils.toRad((float) z));
    }
}