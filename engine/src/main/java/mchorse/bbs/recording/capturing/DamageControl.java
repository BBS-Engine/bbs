package mchorse.bbs.recording.capturing;

import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

/**
 * Damage control
 *
 * This class is responsible for storing damaged blocks and be able to restore
 * them in the world.
 */
public class DamageControl
{
    public List<BlockEntry> blocks = new ArrayList<BlockEntry>();
    public List<Entity> entities = new ArrayList<Entity>();
    public Entity target;

    public int maxDistance;

    public DamageControl(Entity target, int maxDistance)
    {
        this.target = target;
        this.maxDistance = maxDistance;
    }

    public void addBlock(Vector3i pos, BlockVariant variant, World world)
    {
        BasicComponent basic = this.target.basic;

        double x = Math.abs(basic.position.x - pos.x);
        double y = Math.abs(basic.position.y - pos.y);
        double z = Math.abs(basic.position.z - pos.z);

        if (x > this.maxDistance || y > this.maxDistance || z > this.maxDistance)
        {
            return;
        }

        for (BlockEntry entry : this.blocks)
        {
            if (entry.pos.equals(pos))
            {
                return;
            }
        }

        this.blocks.add(new BlockEntry(pos, variant));
    }

    /**
     * Apply recorded damaged blocks back in the world
     */
    public void apply(World world)
    {
        for (BlockEntry entry : this.blocks)
        {
            world.chunks.setBlock(entry.pos.x, entry.pos.y, entry.pos.z, entry.variant);
        }

        for (Entity entity : this.entities)
        {
            entity.remove();
        }

        this.blocks.clear();
        this.entities.clear();
    }

    /**
     * Block entry in the damage control class
     *
     * This class holds information about destroyed block, such as it's state
     */
    public static class BlockEntry
    {
        public Vector3i pos;
        public IBlockVariant variant;

        public BlockEntry(Vector3i pos, BlockVariant variant)
        {
            this.pos = pos;
            this.variant = variant;
        }
    }
}