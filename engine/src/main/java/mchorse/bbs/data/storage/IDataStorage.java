package mchorse.bbs.data.storage;

import mchorse.bbs.data.types.BaseType;

import java.io.IOException;

public interface IDataStorage
{
    public BaseType read() throws IOException;

    public default BaseType readSilently()
    {
        try
        {
            return this.read();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void write(BaseType type) throws IOException;

    public default boolean writeSilently(BaseType type)
    {
        try
        {
            this.write(type);

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}