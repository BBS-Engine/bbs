package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

public class IntArrayType extends BaseType
{
    public static int[] DEFAULT = new int[0];

    public int[] value = DEFAULT;

    public IntArrayType()
    {}

    public IntArrayType(int[] value)
    {
        this.value = value;
    }

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_INT_ARRAY;
    }

    @Override
    public BaseType copy()
    {
        return new IntArrayType(Arrays.copyOf(this.value, this.value.length));
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        int c = context.in.readInt();
        this.value = new int[c];

        byte[] bytes = new byte[c * 4];
        int counter = 0;

        while (counter < bytes.length)
        {
            counter += context.in.read(bytes, counter, bytes.length - counter);
        }

        for (int i = 0; i < c; i++)
        {
            int b1 = bytes[i * 4] & 0xff;
            int b2 = bytes[i * 4 + 1] & 0xff;
            int b3 = bytes[i * 4 + 2] & 0xff;
            int b4 = bytes[i * 4 + 3] & 0xff;

            this.value[i] = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);
        }
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        int c = this.value.length;
        byte[] bytes = new byte[c * 4];

        context.out.writeInt(c);

        for (int i = 0; i < c; i++)
        {
            int value = this.value[i];

            bytes[i * 4] = (byte) (value & 0xff);
            bytes[i * 4 + 1] = (byte) ((value >> 8) & 0xff);
            bytes[i * 4 + 2] = (byte) ((value >> 16) & 0xff);
            bytes[i * 4 + 3] = (byte) ((value >> 24) & 0xff);
        }

        context.out.write(bytes);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IntArrayType)
        {
            IntArrayType array = (IntArrayType) obj;

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

        for (int value : this.value)
        {
            joiner.add(String.valueOf(value));
        }

        return "[i;" + joiner.toString() + "]";
    }
}