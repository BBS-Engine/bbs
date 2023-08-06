package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValueGroup extends BaseValue
{
    private Map<String, BaseValue> children = new LinkedHashMap<>();

    public ValueGroup(String id)
    {
        super(id);
    }

    public void removeAll()
    {
        this.children.clear();
    }

    public List<BaseValue> getAll()
    {
        return new ArrayList<>(this.children.values());
    }

    public void add(BaseValue value)
    {
        if (value != null)
        {
            this.children.put(value.getId(), value);
            value.setParent(this);
        }
    }

    public BaseValue get(String key)
    {
        return this.children.get(key);
    }

    public BaseValue getRecursively(String path)
    {
        BaseValue value = this.children.get(path);

        if (value == null && path.contains("."))
        {
            value = this.searchRecursively(path.split("\\."), path);
        }

        if (value == null)
        {
            throw new IllegalStateException("Property by path " + path + " can't be found!");
        }

        return value;
    }

    private BaseValue searchRecursively(String[] splits, String name)
    {
        int i = 0;
        BaseValue current = this.children.get(splits[i]);

        while (current != null && i < splits.length - 1)
        {
            if (current instanceof ValueGroup)
            {
                i += 1;
                current = ((ValueGroup) current).get(splits[i]);
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

    @Override
    public void reset()
    {
        for (BaseValue value : this.children.values())
        {
            value.reset();
        }
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        for (BaseValue value : this.children.values())
        {
            data.put(value.getId(), value.toData());
        }

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!data.isMap())
        {
            return;
        }

        for (Map.Entry<String, BaseType> entry : data.asMap())
        {
            BaseValue value = this.children.get(entry.getKey());

            if (value != null)
            {
                value.reset();
                value.setParent(this);
                value.fromData(entry.getValue());
            }
        }
    }

    public void copy(ValueGroup group)
    {
        for (Map.Entry<String, BaseValue> entry : group.children.entrySet())
        {
            this.children.get(entry.getKey()).copy(entry.getValue());
        }
    }
}