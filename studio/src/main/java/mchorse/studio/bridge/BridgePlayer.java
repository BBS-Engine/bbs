package mchorse.studio.bridge;

import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.world.entities.Entity;
import mchorse.studio.StudioEngine;

public class BridgePlayer extends BaseBridge implements IBridgePlayer
{
    public BridgePlayer(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public Entity getController()
    {
        return this.engine.controller.getController();
    }

    @Override
    public void setController(Entity entity)
    {
        this.engine.controller.setController(entity);
    }
}