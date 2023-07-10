package mchorse.bbs.utils.manager;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

import java.util.Collection;

public interface IManager <T extends IMapSerializable>
{
    boolean exists(String name);

    public default T create(String id)
    {
        return this.create(id, null);
    }

    public T create(String id, MapType data);

    public T load(String id);

    public boolean save(String name, MapType mapType);

    public boolean rename(String from, String to);

    public boolean delete(String name);

    public Collection<String> getKeys();
}