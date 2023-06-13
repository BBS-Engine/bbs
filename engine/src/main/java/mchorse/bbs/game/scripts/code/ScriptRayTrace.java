package mchorse.bbs.game.scripts.code;

import mchorse.bbs.game.scripts.code.entities.ScriptEntity;
import mchorse.bbs.game.scripts.user.IScriptRayTrace;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class ScriptRayTrace implements IScriptRayTrace
{
    private RayTraceResult result;
    private IScriptEntity entity;

    public static ScriptRayTrace traceFromWorld(World world, Vector3d origin, Vector3f direction, boolean onlyBlocks, double maxDistance)
    {
        RayTraceResult result = new RayTraceResult();

        if (onlyBlocks)
        {
            RayTracer.trace(result, world.chunks, origin, direction, (float) maxDistance);
        }
        else
        {
            RayTracer.traceEntity(result, world, origin, direction, (float) maxDistance);
        }

        return new ScriptRayTrace(result);
    }

    public static ScriptRayTrace traceFromEntity(Entity entity, boolean onlyBlocks, double maxDistance)
    {
        RayTraceResult result = new RayTraceResult();
        Vector3d origin = new Vector3d(entity.basic.position).add(0, entity.basic.getEyeHeight(), 0);

        if (onlyBlocks)
        {
            RayTracer.trace(result, entity.world.chunks, origin, entity.basic.getLook(), (float) maxDistance);
        }
        else
        {
            RayTracer.traceEntity(result, entity.world, origin, entity.basic.getLook(), (float) maxDistance, entity);
        }

        return new ScriptRayTrace(result);
    }

    public ScriptRayTrace(RayTraceResult result)
    {
        this.result = result;
    }

    @Override
    public RayTraceResult getRawRayTraceResult()
    {
        return this.result;
    }

    @Override
    public boolean isMissed()
    {
        return this.result.type == RayTraceType.MISS;
    }

    @Override
    public boolean isBlock()
    {
        return this.result.type == RayTraceType.BLOCK;
    }

    @Override
    public boolean isEntity()
    {
        return this.result.type == RayTraceType.ENTITY;
    }

    @Override
    public IScriptEntity getEntity()
    {
        if (this.result.entity == null)
        {
            return null;
        }

        if (this.entity == null)
        {
            this.entity = ScriptEntity.create(this.result.entity);
        }

        return this.entity;
    }

    @Override
    public Vector3i getBlock()
    {
        return new Vector3i(this.result.block);
    }

    @Override
    public Vector3d getHitPosition()
    {
        return new Vector3d(this.result.hit);
    }
}