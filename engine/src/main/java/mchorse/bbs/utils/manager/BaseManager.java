package mchorse.bbs.utils.manager;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.manager.storage.IDataStorage;
import mchorse.bbs.utils.manager.storage.JSONLikeStorage;

import java.io.File;

/**
 * Base JSON manager which loads and saves different data
 * structures based upon Data API
 */
public abstract class BaseManager <T extends ValueGroup> extends FolderManager<T>
{
    protected IDataStorage storage = new JSONLikeStorage();

    public BaseManager(File folder)
    {
        super(folder);
    }

    @Override
    public final T create(String id, MapType data)
    {
        T object = this.createData(id, data);

        object.setId(id);

        return object;
    }

    protected abstract T createData(String id, MapType mapType);

    @Override
    public T load(String id)
    {
        try
        {
            MapType mapType = this.storage.load(this.getFile(id));
            T data = this.create(id, mapType);

            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(T data)
    {
        return this.save(data.getId(), data.toData().asMap());
    }

    @Override
    public boolean save(String id, MapType data)
    {
        try
        {
            this.storage.save(this.getFile(id), data);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}