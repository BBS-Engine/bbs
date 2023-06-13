package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class ShortType extends NumericType
{
    public short value;

    public ShortType()
    {}

    public ShortType(short value)
    {
        this.value = value;
    }

    /* Numeric type implementation */

    @Override
    public short shortValue()
    {
        return this.value;
    }

    @Override
    public int intValue()
    {
        return this.value;
    }

    @Override
    public float floatValue()
    {
        return this.value;
    }

    @Override
    public long longValue()
    {
        return this.value;
    }

    @Override
    public double doubleValue()
    {
        return this.value;
    }

    /* BaseType implementation */

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_SHORT;
    }

    @Override
    public BaseType copy()
    {
        return new ShortType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readShort();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeShort(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ShortType)
        {
            return this.value == ((ShortType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.value + "s";
    }
}