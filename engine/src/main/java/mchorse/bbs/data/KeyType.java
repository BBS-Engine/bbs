package mchorse.bbs.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public enum KeyType
{
    BYTE((byte) 0)
    {
        @Override
        public int read(DataInputStream stream) throws IOException
        {
            return stream.readByte() & 0xff;
        }

        @Override
        public void write(DataOutputStream stream, int value) throws IOException
        {
            stream.writeByte(value);
        }
    },
    SHORT((byte) 1)
    {
        @Override
        public int read(DataInputStream stream) throws IOException
        {
            return stream.readShort() & 0xffff;
        }

        @Override
        public void write(DataOutputStream stream, int value) throws IOException
        {
            stream.writeShort(value);
        }
    },
    INT((byte) 2)
    {
        @Override
        public int read(DataInputStream stream) throws IOException
        {
            return stream.readInt();
        }

        @Override
        public void write(DataOutputStream stream, int value) throws IOException
        {
            stream.writeInt(value);
        }
    };

    public final byte type;

    public static KeyType from(byte type)
    {
        switch (type)
        {
            case 1: return SHORT;
            case 2: return INT;
        }

        return BYTE;
    }

    private KeyType(byte type)
    {
        this.type = type;
    }

    public abstract int read(DataInputStream stream) throws IOException;

    public abstract void write(DataOutputStream stream, int value) throws IOException;
}