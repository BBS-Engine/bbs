package mchorse.bbs.utils.manager.storage;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;

import java.io.File;
import java.io.IOException;

public class JSONLikeStorage implements IDataStorage
{
    private boolean json;

    public JSONLikeStorage json()
    {
        this.json = true;

        return this;
    }

    @Override
    public MapType load(File file) throws IOException
    {
        return (MapType) DataToString.read(file);
    }

    @Override
    public void save(File file, MapType data) throws IOException
    {
        DataToString.write(file, data, this.json);
    }
}