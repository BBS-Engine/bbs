package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class IntType extends NumericType
{
    public int value;

    public IntType()
    {}

    public IntType(int value)
    {
        this.value = value;
    }

    /* Numeric type implementation */

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
        return BaseType.TYPE_INT;
    }

    @Override
    public BaseType copy()
    {
        return new IntType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readInt();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeInt(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IntType)
        {
            return this.value == ((IntType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.value);
    }
}