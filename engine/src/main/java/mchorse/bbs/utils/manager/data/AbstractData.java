package mchorse.bbs.utils.manager.data;

import mchorse.bbs.data.IMapSerializable;

public abstract class AbstractData implements IMapSerializable, IID
{
    private String id;

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }
}