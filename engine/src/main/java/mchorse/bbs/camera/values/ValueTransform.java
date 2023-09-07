package mchorse.bbs.camera.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.utils.Transform;

public class ValueTransform extends BaseValueBasic<Transform>
{
    public ValueTransform(String id, Transform transform)
    {
        super(id);

        this.set(transform);
    }

    @Override
    public BaseType toData()
    {
        return this.value.toData();
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value.fromData(data.asMap());
    }
}