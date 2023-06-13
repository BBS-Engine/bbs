package mchorse.bbs.data.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataGzipStorage extends DataStorage
{
    private DataStorage storage;

    public DataGzipStorage(DataStorage storage)
    {
        this.storage = storage;
    }

    @Override
    protected InputStream getInputStream() throws IOException
    {
        return new GZIPInputStream(this.storage.getInputStream());
    }

    @Override
    protected OutputStream getOutputStream() throws IOException
    {
        return new GZIPOutputStream(this.storage.getOutputStream());
    }
}
