package mchorse.bbs.camera.values;

import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.data.types.BaseType;

public class ValueAngle extends BaseValueBasic<Angle>
{
    public ValueAngle(String id, Angle angle)
    {
        super(id);

        this.set(angle);
    }

    @Override
    public void reset()
    {
        this.value.set(0, 0, 0, 70);
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