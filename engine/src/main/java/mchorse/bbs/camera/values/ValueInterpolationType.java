package mchorse.bbs.camera.values;

import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.settings.values.base.BaseValueDefault;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;

public class ValueInterpolationType extends BaseValueDefault<InterpolationType>
{
    public ValueInterpolationType(String id)
    {
        super(id, InterpolationType.HERMITE);
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value.name);
    }

    @Override
    public void fromData(BaseType base)
    {
        String key = base.asString();

        for (InterpolationType type : InterpolationType.values())
        {
            if (type.name.equals(key))
            {
                this.value = type;

                break;
            }
        }
    }
}