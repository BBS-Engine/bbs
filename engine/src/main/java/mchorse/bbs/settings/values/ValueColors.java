package mchorse.bbs.settings.values;

import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.utils.colors.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ValueColors extends BaseValue implements IParseableValue
{
    private List<Color> colors = new ArrayList<Color>();

    public ValueColors(String id)
    {
        super(id);
    }

    public List<Color> getCurrentColors()
    {
        return this.colors;
    }

    @Override
    public void reset()
    {
        this.colors.clear();
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
    public boolean parse(String value)
    {
        String[] splits = value.split(",");
        List<Color> colors = new ArrayList<Color>();

        for (String split : splits)
        {
            try
            {
                int color = Colors.parseWithException(split.trim());

                colors.add(new Color().set(color));
            }
            catch (Exception e)
            {
                return false;
            }
        }

        this.colors.clear();
        this.colors.addAll(colors);
        this.notifyParent();

        return true;
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