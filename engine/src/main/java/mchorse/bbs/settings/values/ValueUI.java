package mchorse.bbs.settings.values;

import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;

public abstract class ValueUI extends BaseValue implements IValueUIProvider
{
    public ValueUI(String id)
    {
        super(id);
    }

    @Override
    public BaseType toData()
    {
        return new ByteType(false);
    }

    @Override
    public void fromData(BaseType data)
    {}
}