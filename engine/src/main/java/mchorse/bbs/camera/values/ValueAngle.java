package mchorse.bbs.camera.values;

import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.base.BaseValueBasic;

public class ValueAngle extends BaseValueBasic<Angle>
{
    public ValueAngle(String id, Angle angle)
    {
        super(id, angle);
    }

    @Override
    public void set(Angle value)
    {
        this.preNotifyParent();
        this.value.set(value);
        this.postNotifyParent();
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