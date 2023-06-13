package mchorse.bbs.camera.data;

import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.data.AbstractData;

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
        BaseValue value = this.category.add(name);

        if (value == null && name.contains("."))
        {
            String[] splits = name.split("\\.");

            value = this.searchRecursively(splits, name);
        }

        if (value == null)
        {
            throw new IllegalStateException("Property by name " + name + " can't be found!");
        }

        return value;
    }

    private BaseValue searchRecursively(String[] splits, String name)
    {
        int i = 0;
        BaseValue current = this.category.add(splits[i]);

        while (current != null && i < splits.length - 1)
        {
            if (current instanceof ValueGroup)
            {
                i += 1;
                current = ((ValueGroup) current).add(splits[i]);
            }
            else
            {
                current = null;
            }
        }

        if (current == null)
        {
            return null;
        }

        return current.getPath().equals(name) ? current : null;
    }

    public void copy(StructureBase base)
    {
        for (BaseValue value : this.category.getAll())
        {
            BaseValue from = base.category.add(value.getId());

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