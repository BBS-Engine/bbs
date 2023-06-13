package mchorse.bbs.audio;

import java.io.IOException;
import java.io.InputStream;

public abstract class BinaryReader
{
    public byte[] buf = new byte[4];

    public static int b2i(byte b0, byte b1, byte b2, byte b3)
    {
        return (b0 & 0xff) | ((b1 & 0xff) << 8) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 24);
    }

    public int fourChars(char c0, char c1, char c2, char c3)
    {
        return ((c3 << 24) & 0xff000000) | ((c2 << 16) & 0x00ff0000) | ((c1 << 8) & 0x0000ff00) | (c0 & 0x000000ff);
    }

    public int fourChars(String string) throws Exception
    {
        char[] chars = string.toCharArray();

        if (chars.length != 4)
        {
            throw new Exception("Given string '" + string + "'");
        }

        return this.fourChars(chars[0], chars[1], chars[2], chars[3]);
    }

    public String readFourString(InputStream stream) throws Exception
    {
        stream.read(this.buf);

        return new String(this.buf);
    }

    public int readInt(InputStream stream) throws Exception
    {
        if (stream.read(this.buf) < 4)
        {
            throw new IOException();
        }

        return b2i(this.buf[0], this.buf[1], this.buf[2], this.buf[3]);
    }

    public int readShort(InputStream stream) throws Exception
    {
        if (stream.read(this.buf, 0, 2) < 2)
        {
            throw new IOException();
        }

        return b2i(this.buf[0], this.buf[1], (byte) 0, (byte) 0);
    }

    public void skip(InputStream stream, long bytes) throws Exception
    {
        while (bytes > 0)
        {
            bytes -= stream.skip(bytes);
        }
    }
}