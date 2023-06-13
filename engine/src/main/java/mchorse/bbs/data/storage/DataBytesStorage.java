package mchorse.bbs.data.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBytesStorage extends DataStorage
{
    protected byte[] input;
    protected ByteArrayOutputStream output;

    public void setBytes(byte[] bytes)
    {
        this.input = bytes;
    }

    public byte[] getBytes()
    {
        return this.output.toByteArray();
    }

    @Override
    protected InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(this.input);
    }

    @Override
    protected OutputStream getOutputStream() throws IOException
    {
        this.output = new ByteArrayOutputStream();

        return this.output;
    }
}