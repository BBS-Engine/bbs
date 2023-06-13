package mchorse.bbs.world;

import mchorse.bbs.utils.AABB;

public interface IWorldObject
{
    public AABB getPickingHitbox();
}