package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

public class ByteArrayType extends BaseType
{
    public static byte[] DEFAULT = new byte[0];

    public byte[] value = DEFAULT;

    public ByteArrayType()
    {}

    public ByteArrayType(byte[] value)
    {
        this.value = value;
    }

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_BYTE_ARRAY;
    }

    @Override
    public BaseType copy()
    {
        return new ByteArrayType(Arrays.copyOf(this.value, this.value.length));
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        int c = context.in.readInt();
        this.value = new byte[c];

        int counter = 0;

        while (counter < this.value.length)
        {
            counter += context.in.read(this.value, counter, this.value.length - counter);
        }
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeInt(this.value.length);
        context.out.write(this.value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ByteArrayType)
        {
            ByteArrayType array = (ByteArrayType) obj;

            if (array.value.length != this.value.length)
            {
                return false;
            }

            for (int i = 0; i < this.value.length; i++)
            {
                if (this.value[i] != array.value[i])
                {
                    return false;
                }
            }

            return true;
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(",");

        for (byte value : this.value)
        {
            joiner.add(value + "b");
        }

        return "[b;" + joiner.toString() + "]";
    }
}