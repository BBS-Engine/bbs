package mchorse.bbs.utils.factory;

import mchorse.bbs.resources.Link;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic implementation of factory based on a map of
 * String key and Class value
 */
public class MapFactory <T, D> implements IFactory<T, D>
{
    protected Map<Link, Class<? extends T>> factory = new LinkedHashMap<>();
    protected Map<Class<? extends T>, Link> factoryInverse = new LinkedHashMap<>();

    protected Map<Link, D> data = new HashMap<>();

    public MapFactory<T, D> copy()
    {
        MapFactory<T, D> factory = new MapFactory<>();

        for (Map.Entry<Link, Class<? extends T>> entry : this.factory.entrySet())
        {
            factory.register(entry.getKey(), entry.getValue(), this.data.get(entry.getValue()));
        }

        return factory;
    }

    public MapFactory<T, D> register(Link type, Class<? extends T> clazz)
    {
        return this.register(type, clazz, null);
    }

    public MapFactory<T, D> register(Link type, Class<? extends T> clazz, D data)
    {
        this.factory.put(type, clazz);
        this.factoryInverse.put(clazz, type);
        this.data.put(type, data);

        return this;
    }

    public MapFactory<T, D> unregister(String key)
    {
        Class<? extends T> clazz = this.factory.remove(key);

        this.factoryInverse.remove(clazz);
        this.data.remove(clazz);

        return this;
    }

    public Link getTypeSilent(T object)
    {
        return this.factoryInverse.get(object.getClass());
    }

    @Override
    public Link getType(T object)
    {
        Link type = this.factoryInverse.get(object.getClass());

        if (type != null)
        {
            return type;
        }

        throw new IllegalStateException("Object " + object.getClass() + " is not part of this factory!");
    }

    public Class<? extends T> getTypeClass(String type)
    {
        return this.getTypeClass(Link.create(type));
    }

    public Class<? extends T> getTypeClass(Link type)
    {
        return this.factory.get(type);
    }

    @Override
    public T create(Link type)
    {
        Class<? extends T> clazz = this.factory.get(type);

        if (clazz != null)
        {
            try
            {
                return clazz.getConstructor().newInstance();
            }
            catch (Exception e)
            {}
        }

        throw new IllegalStateException("Object type " + type + " is not part of this factory!");
    }

    @Override
    public D getData(T object)
    {
        return this.data.get(this.getTypeSilent(object));
    }

    @Override
    public D getData(Link type)
    {
        return this.data.get(type);
    }

    @Override
    public Collection<Link> getKeys()
    {
        return this.factory.keySet();
    }
}
