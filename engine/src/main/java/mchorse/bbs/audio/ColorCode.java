package mchorse.bbs.audio;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.ListType;

public class ColorCode implements IDataSerializable<ListType>
{
    public float start;
    public float end;
    public int color;

    public ColorCode()
    {}

    public ColorCode(float start, float end, int color)
    {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public boolean isInside(float time)
    {
        return time >= this.start && time < this.end;
    }

    @Override
    public ListType toData()
    {
        ListType list = new ListType();

        list.addFloat(this.start);
        list.addFloat(this.end);
        list.addInt(this.color);

        return list;
    }

    @Override
    public void fromData(ListType data)
    {
        if (data.size() >= 3)
        {
            this.start = data.getFloat(0);
            this.end = data.getFloat(1);
            this.color = data.getInt(2);
        }
    }
}