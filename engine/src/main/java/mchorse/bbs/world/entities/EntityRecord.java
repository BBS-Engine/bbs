package mchorse.bbs.world.entities;

import mchorse.bbs.world.entities.components.Component;

public class EntityRecord
{
    public Class<?> key;
    public Component component;
    public int index;

    public EntityRecord(Class<?> key, Component component)
    {
        this.key = key;
        this.component = component;
    }

    public EntityRecord(Class<?> key, Component component, int index)
    {
        this(key, component);

        this.index = index;
    }
}