package mchorse.bbs.data;

import mchorse.bbs.data.types.BaseType;

public interface IDataSerializable <T extends BaseType>
{
    public T toData();

    public void fromData(T data);
}