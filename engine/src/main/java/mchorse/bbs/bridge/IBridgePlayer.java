package mchorse.bbs.bridge;

import mchorse.bbs.world.entities.Entity;

public interface IBridgePlayer
{
    public boolean isDevelopment();

    public boolean isCreative();

    public void setCreative(boolean creative);

    public Entity getController();

    public void setController(Entity entity);
}