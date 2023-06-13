package mchorse.bbs.game.utils.manager;

import mchorse.bbs.data.types.MapType;

public class ManagerCache
{
    public MapType data;
    public long lastUpdated;
    public long lastUsed;

    public ManagerCache(MapType data, long lastUpdated)
    {
        this.data = data;
        this.lastUpdated = lastUpdated;
        this.update();
    }

    public void update()
    {
        this.lastUsed = System.currentTimeMillis();
    }
}