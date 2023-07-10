package mchorse.bbs.utils.manager;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.utils.manager.storage.IDataStorage;
import mchorse.bbs.utils.manager.storage.JSONLikeStorage;

import java.io.File;

/**
 * Base JSON manager which loads and saves different data
 * structures based upon Data API
 */
public abstract class BaseManager <T extends AbstractData> extends FolderManager<T>
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
            MapType mapType = this.getCached(id);
            T data = this.create(id, mapType);

            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get cached data map from given file by ID
     */
    protected MapType getCached(String id) throws Exception
    {
        MapType map = null;
        File file = this.getFile(id);
        boolean isCaching = this.canCache();
        long lastUpdated = file.lastModified();

        if (isCaching)
        {
            ManagerCache cache = this.cache.get(id);

            if (cache != null)
            {
                /* This is necessary for update if the files were edited externally,
                 * because dashboard save will clear the cache for sure */
                if (cache.lastUpdated < lastUpdated)
                {
                    this.cache.remove(id);
                }
                else
                {
                    map = cache.data;

                    cache.update();
                }

                this.doExpirationCheck();
            }
        }

        if (map == null)
        {
            map = this.storage.load(file);

            if (isCaching)
            {
                this.cache.put(id, new ManagerCache(map, lastUpdated));
            }
        }

        return map;
    }

    public boolean save(T data)
    {
        return this.save(data.getId(), data.toData());
    }

    @Override
    public boolean save(String id, MapType data)
    {
        try
        {
            this.storage.save(this.getFile(id), data);
            this.cache.remove(id);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}