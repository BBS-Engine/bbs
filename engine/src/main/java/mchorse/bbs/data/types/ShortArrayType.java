package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

public class ShortArrayType extends BaseType
{
    public static short[] DEFAULT = new short[0];

    public short[] value = DEFAULT;

    public ShortArrayType()
    {}

    public ShortArrayType(short[] value)
    {
        this.value = value;
    }

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_SHORT_ARRAY;
    }

    @Override
    public BaseType copy()
    {
        return new ShortArrayType(Arrays.copyOf(this.value, this.value.length));
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        int c = context.in.readInt();
        this.value = new short[c];

        byte[] bytes = new byte[c * 2];
        int counter = 0;

        while (counter < bytes.length)
        {
            counter += context.in.read(bytes, counter, bytes.length - counter);
        }

        for (int i = 0; i < c; i++)
        {
            int b1 = bytes[i * 2] & 0xff;
            int b2 = bytes[i * 2 + 1] & 0xff;

            this.value[i] = (short) (b1 | (b2 << 8));
        }
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        int c = this.value.length;
        byte[] bytes = new byte[c * 2];

        context.out.writeInt(c);

        for (int i = 0; i < c; i++)
        {
            short value = this.value[i];

            bytes[i * 2] = (byte) (value & 0xff);
            bytes[i * 2 + 1] = (byte) (value >> 8);
        }

        context.out.write(bytes);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ShortArrayType)
        {
            ShortArrayType array = (ShortArrayType) obj;

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

        for (short value : this.value)
        {
            joiner.add(value + "s");
        }

        return "[s;" + joiner.toString() + "]";
    }
}