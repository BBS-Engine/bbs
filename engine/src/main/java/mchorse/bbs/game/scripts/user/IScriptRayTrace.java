package mchorse.bbs.game.scripts.user;

import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector3d;
import org.joml.Vector3i;

/**
 * This interface represents a ray tracing result.
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var result = c.getSubject().rayTrace(32);
 *
 *        if (result.isBlock())
 *        {
 *            // Ray hit a block, so we'll change the block to diamond block
 *            var pos = result.getBlock();
 *            var world = bbs.worlds.getCurrent();
 *
 *            world.setBlock(10, pos.x, pos.y, pos.z);
 *        }
 *        else if (result.isEntity())
 *        {
 *            // Ray hit an entity, so we'll throw it in the air
 *            result.getEntity().setVelocity(0, 1, 0);
 *        }
 *        else // if (result.isMissed())
 *        {
 *            bbs.send("Good luck next time!");
 *        }
 *    }
 * }</pre>
 */
public interface IScriptRayTrace
{
    /**
     * Get raw ray trace result.
     */
    public RayTraceResult getRawRayTraceResult();

    /**
     * Check whether this ray trace result didn't capture anything.
     */
    public boolean isMissed();

    /**
     * Check whether this ray trace result hit a block.
     */
    public boolean isBlock();

    /**
     * Check whether this ray trace result hit an entity.
     */
    public boolean isEntity();

    /**
     * Get entity that was captured by this ray trace result (it can be null).
     */
    public IScriptEntity getEntity();

    /**
     * Get block position it hit.
     */
    public Vector3i getBlock();

    /**
     * Get precise position where it hit.
     */
    public Vector3d getHitPosition();
}