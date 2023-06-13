package mchorse.bbs.voxel.raytracing;

import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class RayTraceResult
{
    public RayTraceType type = RayTraceType.MISS;
    public Entity entity;
    public WorldObject object;
    public Vector3d origin = new Vector3d();
    public Vector3d hit = new Vector3d();
    public Vector3i normal = new Vector3i();
    public Vector3i block = new Vector3i();

    public void reset()
    {
        this.type = RayTraceType.MISS;
        this.entity = null;
        this.object = null;
        this.origin.set(0, 0, 0);
        this.hit.set(0, 0, 0);
        this.normal.set(0, 0, 0);
        this.block.set(0, 0, 0);
    }
}