package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class FloatType extends NumericType
{
    public float value;

    public FloatType()
    {}

    public FloatType(float value)
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
        return this.value;
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
        return BaseType.TYPE_FLOAT;
    }

    @Override
    public BaseType copy()
    {
        return new FloatType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readFloat();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeFloat(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FloatType)
        {
            return this.value == ((FloatType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.value + "f";
    }
}