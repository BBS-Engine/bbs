package mchorse.bbs.camera.data;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.manager.data.AbstractData;

import java.util.Collection;

public abstract class StructureBase extends AbstractData
{
    protected ValueGroup category = new ValueGroup("");

    protected void register(BaseValue value)
    {
        this.category.add(value);
    }

    public Collection<BaseValue> getProperties()
    {
        return this.category.getAll();
    }

    public BaseValue getProperty(String name)
    {
        return this.category.getRecursively(name);
    }

    public void copy(StructureBase base)
    {
        for (BaseValue value : this.category.getAll())
        {
            BaseValue from = base.category.get(value.getId());

            if (from != null)
            {
                value.copy(from);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.combine(this.category.toData().asMap());
    }

    @Override
    public void fromData(MapType data)
    {
        this.category.fromData(data);
    }
}