package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.ui.framework.elements.input.UITransform;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.math.MathUtils;

import java.util.function.Consumer;

public class UIPropTransform extends UITransform
{
    private Transform transform;
    private Consumer<Transform> callback;

    public UIPropTransform()
    {}

    public UIPropTransform(Consumer<Transform> callback)
    {
        this.callback = callback;
    }

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

        if (this.callback != null)
        {
            this.callback.accept(this.transform);
        }
    }

    @Override
    public void setS(double x, double y, double z)
    {
        this.transform.scale.set((float) x, (float) y, (float) z);

        if (this.callback != null)
        {
            this.callback.accept(this.transform);
        }
    }

    @Override
    public void setR(double x, double y, double z)
    {
        this.transform.rotate.set(MathUtils.toRad((float) x), MathUtils.toRad((float) y), MathUtils.toRad((float) z));

        if (this.callback != null)
        {
            this.callback.accept(this.transform);
        }
    }
}