package mchorse.bbs.world.entities.components;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.world.entities.Entity;

public abstract class Component implements IMapSerializable
{
    protected Entity entity;

    public Entity getEntity()
    {
        return this.entity;
    }

    public void setEntity(Entity entity)
    {
        this.entity = entity;
    }

    public Link getId()
    {
        return BBS.getFactoryEntityComponents().getType(this);
    }

    public void entityWasRemoved()
    {}

    public void preUpdate()
    {}

    public void postUpdate()
    {}

    @Override
    public void toData(MapType data)
    {}

    @Override
    public void fromData(MapType data)
    {}
}