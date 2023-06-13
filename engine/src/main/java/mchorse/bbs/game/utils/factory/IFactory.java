package mchorse.bbs.game.utils.factory;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IFactory <T, D>
{
    public Link getType(T object);

    public T create(Link type);

    public default MapType toData(T object)
    {
        MapType data = new MapType();

        if (object instanceof IMapSerializable)
        {
            ((IMapSerializable) object).toData(data);

            data.putString(this.getTypeKey(), this.getType(object).toString());
        }

        return data;
    }

    public default T fromData(MapType data)
    {
        if (data == null)
        {
            return null;
        }

        Link type = Link.create(data.getString(this.getTypeKey()));
        T object = this.create(type);

        if (object instanceof IMapSerializable)
        {
            ((IMapSerializable) object).fromData(data);
        }

        return object;
    }

    public default String getTypeKey()
    {
        return "type";
    }

    public D getData(T object);

    public D getData(Link type);

    public Collection<Link> getKeys();

    public default Collection<String> getStringKeys()
    {
        Set<String> keys = new HashSet<String>();

        for (Link link : this.getKeys())
        {
            keys.add(link.toString());
        }

        return keys;
    }
}