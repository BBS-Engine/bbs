package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class DoubleType extends NumericType
{
    public double value;

    public DoubleType()
    {}

    public DoubleType(double value)
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
        return (long) this.value;
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
        return BaseType.TYPE_DOUBLE;
    }

    @Override
    public BaseType copy()
    {
        return new DoubleType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readDouble();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeDouble(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof DoubleType)
        {
            return this.value == ((DoubleType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.value + "d";
    }
}