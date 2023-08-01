package mchorse.bbs.bridge;

import mchorse.bbs.world.entities.Entity;

public interface IBridgePlayer
{
    public Entity getController();

    public void setController(Entity entity);
}