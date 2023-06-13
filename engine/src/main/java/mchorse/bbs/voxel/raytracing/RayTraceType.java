package mchorse.bbs.voxel.raytracing;

public enum RayTraceType
{
    BLOCK, ENTITY, OBJECT, MISS;

    public boolean isMissed()
    {
        return this == MISS;
    }
}