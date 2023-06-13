package mchorse.bbs.game.quests.rewards;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.world.entities.Entity;

public abstract class Reward implements IMapSerializable
{
    public abstract void reward(Entity player);

    public abstract Reward copy();
}