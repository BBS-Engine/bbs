package mchorse.bbs.camera.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.utils.math.Interpolation;

public class ValueInterpolation extends BaseValueBasic<Interpolation>
{
    public ValueInterpolation(String id)
    {
        super(id, Interpolation.LINEAR);
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value.toString());
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value = Interpolation.valueOf(data.asString());
    }
}