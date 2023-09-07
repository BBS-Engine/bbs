package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.base.BaseValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ValueList <T extends BaseValue> extends ValueGroup
{
    protected List<T> list = new ArrayList<T>();

    public ValueList(String id)
    {
        super(id);
    }

    public List<T> getList()
    {
        return Collections.unmodifiableList(this.list);
    }

    protected abstract T create(String id);

    protected void sync()
    {
        this.removeAll();

        int i = 0;

        for (T value : this.list)
        {
            value.setId(String.valueOf(i));
            this.add(value);

            i += 1;
        }
    }

    @Override
    public void fromData(BaseType data)
    {
        this.removeAll();

        for (String key : data.asMap().keys())
        {
            T value = this.create(key);

            this.list.add(value);
            this.add(value);
        }

        super.fromData(data);
    }
}