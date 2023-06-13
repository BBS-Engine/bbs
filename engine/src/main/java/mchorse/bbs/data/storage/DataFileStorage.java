package mchorse.bbs.data.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataFileStorage extends DataStorage
{
    protected File file;

    public DataFileStorage(File file)
    {
        this.file = file;
    }

    public File getFile()
    {
        return this.file;
    }

    @Override
    protected InputStream getInputStream() throws IOException
    {
        return new FileInputStream(this.file);
    }

    @Override
    protected OutputStream getOutputStream() throws IOException
    {
        return new FileOutputStream(this.file);
    }
}