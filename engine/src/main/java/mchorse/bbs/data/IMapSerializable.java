package mchorse.bbs.data;

import mchorse.bbs.data.types.MapType;

public interface IMapSerializable extends IDataSerializable<MapType>
{
    public default MapType toData()
    {
        MapType map = new MapType();

        this.toData(map);

        return map;
    }

    public void toData(MapType data);
}