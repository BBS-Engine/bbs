package mchorse.bbs.game.quests;

import mchorse.bbs.data.types.MapType;

public interface IMapPartialSerializable
{
    public default MapType partialToData()
    {
        MapType data = new MapType();

        this.partialToData(data);

        return data;
    }

    public void partialToData(MapType data);

    public void partialFromData(MapType data);
}