package mchorse.bbs.utils.resources;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.resources.Link;

public interface IWritableLink extends IDataSerializable<BaseType>
{
    public Link copy();
}