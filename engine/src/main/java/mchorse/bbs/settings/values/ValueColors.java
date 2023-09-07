package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.colors.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ValueColors extends BaseValue
{
    private List<Color> colors = new ArrayList<>();

    public ValueColors(String id)
    {
        super(id);
    }

    public List<Color> getCurrentColors()
    {
        return this.colors;
    }

    public void addColor(Color color)
    {
        int i = this.colors.indexOf(color);

        if (i == -1)
        {
            this.preNotifyParent(this);
            this.colors.add(color.copy());
            this.postNotifyParent(this);
        }
    }

    public void remove(int index)
    {
        this.preNotifyParent(this);
        this.colors.remove(index);
        this.postNotifyParent(this);
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (Color color : this.colors)
        {
            list.addInt(color.getARGBColor());
        }

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!BaseType.isList(data))
        {
            return;
        }

        ListType list = (ListType) data;

        for (BaseType color : list)
        {
            if (color.isNumeric())
            {
                this.colors.add(new Color().set(color.asNumeric().intValue()));
            }
        }
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(", ");

        for (Color color : this.colors)
        {
            joiner.add("#" + Integer.toHexString(color.getARGBColor()));
        }

        return joiner.toString();
    }
}