package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.settings.values.base.BaseValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ValueList <T extends BaseValue> extends ValueGroup
{
    protected final List<T> list = new ArrayList<T>();

    public ValueList(String id)
    {
        super(id);
    }

    public List<T> getList()
    {
        return Collections.unmodifiableList(this.list);
    }

    protected abstract T create(String id);

    public void sync()
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
    public BaseType toData()
    {
        ListType list = new ListType();

        for (T value : this.list)
        {
            list.add(value.toData());
        }

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.list.clear();
        this.removeAll();

        if (data.isMap())
        {
            /* Backward compatibility with maps */
            for (String key : data.asMap().keys())
            {
                T value = this.create(key);

                this.list.add(value);
                this.add(value);
            }

            super.fromData(data);
        }
        else if (data.isList())
        {
            ListType list = data.asList();

            for (int i = 0; i < list.size(); i++)
            {
                T value = this.create(String.valueOf(i));

                this.list.add(value);
                this.add(value);

                value.fromData(list.get(i));
            }
        }
    }
}