package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public class ByteType extends NumericType
{
    public byte value;

    public ByteType()
    {}

    public ByteType(boolean value)
    {
        this.value = value ? (byte) 1 : (byte) 0;
    }

    public ByteType(byte value)
    {
        this.value = value;
    }

    /* Numeric type implementation */

    @Override
    public byte byteValue()
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
        return BaseType.TYPE_BYTE;
    }

    @Override
    public BaseType copy()
    {
        return new ByteType(this.value);
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.value = context.in.readByte();
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeByte(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ByteType)
        {
            return this.value == ((ByteType) obj).value;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return this.value + "b";
    }
}