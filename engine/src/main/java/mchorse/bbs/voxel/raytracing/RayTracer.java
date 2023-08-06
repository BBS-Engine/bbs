package mchorse.bbs.voxel.raytracing;

import mchorse.bbs.utils.AABB;
import mchorse.bbs.voxel.IBlockAccessor;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import mchorse.bbs.world.IWorldObject;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class RayTracer
{
    private static final AABB aabb = new AABB();

    public static void traceEntity(RayTraceResult result, World world, Vector3d origin, Vector3f direction, float maxReach)
    {
        traceEntity(result, world, origin, direction, maxReach, null);
    }

    public static void traceEntity(RayTraceResult result, World world, Vector3d origin, Vector3f direction, float maxReach, Entity exception)
    {
        traceEntity(result, world, origin, direction, maxReach, exception, false);
    }

    public static void traceEntity(RayTraceResult result, World world, Vector3d origin, Vector3f direction, float maxReach, IWorldObject exception, boolean includeObjects)
    {
        trace(result, world.chunks, origin, direction, maxReach);

        if (result.type.isMissed())
        {
            result.reset();
        }

        List<IWorldObject> entities = new ArrayList<>();

        for (Entity entity : world.entities)
        {
            if (entity == exception)
            {
                continue;
            }

            AABB aabb = entity.basic.hitbox;

            if (aabb.intersectsRay(origin, direction))
            {
                entities.add(entity);
            }
        }

        if (includeObjects)
        {
            for (WorldObject object : world.objects)
            {
                if (object == exception)
                {
                    continue;
                }

                AABB aabb = object.getPickingHitbox();

                if (aabb.intersectsRay(origin, direction))
                {
                    entities.add(object);
                }
            }
        }

        if (!entities.isEmpty())
        {
            if (entities.size() > 1)
            {
                entities.sort(Comparator.comparingDouble(a ->
                {
                    AABB aabb = a.getPickingHitbox();

                    return new Vector3d(aabb.x, aabb.y, aabb.z).distanceSquared(origin);
                }));
            }

            IWorldObject object = entities.get(0);

            if (object instanceof Entity)
            {
                result.type = RayTraceType.ENTITY;
                result.entity = (Entity) entities.get(0);
            }
            else if (object instanceof WorldObject)
            {
                result.type = RayTraceType.OBJECT;
                result.object = (WorldObject) entities.get(0);
            }
        }
    }

    public static void trace(RayTraceResult result, IBlockAccessor blockAccessor, Vector3d origin, Vector3f direction, float maxReach)
    {
        trace(result, blockAccessor, origin, direction, maxReach, false, null);
    }

    public static void trace(RayTraceResult result, IBlockAccessor blockAccessor, Vector3d origin, Vector3f direction, float maxReach, boolean ignoreFirst, Function<RayTraceResult, Boolean> accept)
    {
        double t = 0;
        int x = (int) Math.floor(origin.x);
        int y = (int) Math.floor(origin.y);
        int z = (int) Math.floor(origin.z);

        int stepX = direction.x > 0 ? 1 : -1;
        int stepY = direction.y > 0 ? 1 : -1;
        int stepZ = direction.z > 0 ? 1 : -1;

        float txDelta = Math.abs(1 / direction.x);
        float tyDelta = Math.abs(1 / direction.y);
        float tzDelta = Math.abs(1 / direction.z);

        double distX = stepX > 0 ? x + 1 - origin.x : origin.x - x;
        double distY = stepY > 0 ? y + 1 - origin.y : origin.y - y;
        double distZ = stepZ > 0 ? z + 1 - origin.z : origin.z - z;

        double txMax = txDelta < Float.POSITIVE_INFINITY ? txDelta * distX : Float.POSITIVE_INFINITY;
        double tyMax = tyDelta < Float.POSITIVE_INFINITY ? tyDelta * distY : Float.POSITIVE_INFINITY;
        double tzMax = tzDelta < Float.POSITIVE_INFINITY ? tzDelta * distZ : Float.POSITIVE_INFINITY;

        int iterations = 0;
        int collisions = 0;

        result.reset();
        result.origin.set(origin);

        while (t <= maxReach)
        {
            if (checkForBlock(result, blockAccessor, x, y, z, origin, direction))
            {
                if (!ignoreFirst || iterations != collisions)
                {
                    result.type = RayTraceType.BLOCK;
                    result.block.set(x, y, z);

                    if (accept == null || accept.apply(result))
                    {
                        return;
                    }

                    result.type = RayTraceType.MISS;
                }

                collisions += 1;
            }

            if (txMax < tyMax)
            {
                if (txMax < tzMax)
                {
                    x += stepX;
                    t = txMax;
                    txMax += txDelta;
                }
                else
                {
                    z += stepZ;
                    t = tzMax;
                    tzMax += tzDelta;
                }
            }
            else
            {
                if (tyMax < tzMax)
                {
                    y += stepY;
                    t = tyMax;
                    tyMax += tyDelta;
                }
                else
                {
                    z += stepZ;
                    t = tzMax;
                    tzMax += tzDelta;
                }
            }

            iterations += 1;
        }

        result.type = RayTraceType.MISS;
    }

    public static boolean checkForBlock(RayTraceResult result, IBlockAccessor blockAccessor, int x, int y, int z, Vector3d origin, Vector3f direction)
    {
        if (blockAccessor.hasBlock(x, y, z))
        {
            IBlockVariant variant = blockAccessor.getBlock(x, y, z);
            BlockModel model = variant.getModel();

            aabb.set(model.collisionBox);
            aabb.x += x;
            aabb.y += y;
            aabb.z += z;

            if (aabb.intersectsRayHitNormal(origin, direction, result.hit, result.normal))
            {
                return true;
            }
        }

        return false;
    }
}