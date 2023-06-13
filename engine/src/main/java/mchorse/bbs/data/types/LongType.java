package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class LongType extends NumericType
{
    public long value;

    public LongType()
    {}

    public LongType(long value)
    {
        this.value = value;
    }

    /* Numeric type implementation */

    @Override
    public int intValue()
    {
        return (int) this.value;
    }

    @Override
    public float floatValue()
    {
        return (float) this.value;
    }

    @Override
    public long longValue()
    {
        return this.value;
    }

    @Override
    public double doubleValue()
    {
        return (double) this.value;
    }

    /* BaseType implementation */

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_LONG;
    }

    @Override
    public BaseType copy()
    {
        return new LongType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readLong();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeLong(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof LongType)
        {
            return this.value == ((LongType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.value + "l";
    }
}