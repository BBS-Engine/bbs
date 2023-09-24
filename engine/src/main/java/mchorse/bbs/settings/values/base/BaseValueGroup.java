package mchorse.bbs.settings.values.base;

import java.util.List;

public abstract class BaseValueGroup extends BaseValue
{
    public BaseValueGroup(String id)
    {
        super(id);
    }

    public abstract List<BaseValue> getAll();

    public abstract BaseValue get(String key);

    public BaseValue getRecursively(String path)
    {
        BaseValue value = this.get(path);

        if (value == null && path.contains("."))
        {
            value = this.searchRecursively(path.split("\\."));
        }

        if (value == null)
        {
            throw new IllegalStateException("Property by path " + path + " can't be found!");
        }

        return value;
    }

    private BaseValue searchRecursively(String[] splits)
    {
        int i = 0;
        BaseValue current = this;

        while (current != null && i < splits.length - 1)
        {
            if (current instanceof BaseValueGroup)
            {
                i += 1;
                current = ((BaseValueGroup) current).get(splits[i]);
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

        return current;
    }

    public abstract void copy(BaseValueGroup group);
}