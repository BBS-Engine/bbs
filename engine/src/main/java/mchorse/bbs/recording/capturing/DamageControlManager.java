package mchorse.bbs.recording.capturing;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

/**
 * Damage control manager
 *
 * This class is responsible for managing damage control
 */
public class DamageControlManager
{
    /**
     * Damage control objects
     */
    public Map<Object, DamageControl> damage = new HashMap<>();

    public void reset()
    {
        this.damage.clear();
    }

    /**
     * Start observing damage made to terrain
     */
    public void addDamageControl(Object object, Entity player)
    {
        if (BBSSettings.damageControl.get())
        {
            int dist = BBSSettings.damageControlDistance.get();

            this.damage.put(object, new DamageControl(player, dist));
        }
    }

    /**
     * Restore inflicted damage
     */
    public void restoreDamageControl(Object object, World world)
    {
        DamageControl control = this.damage.remove(object);

        if (control != null)
        {
            control.apply(world);
        }
    }

    /**
     * Add an entity to track
     */
    public void addEntity(Entity entity)
    {
        for (DamageControl damage : this.damage.values())
        {
            damage.entities.add(entity);
        }
    }

    /**
     * Add a block to track
     */
    public void addBlock(Vector3i pos, BlockVariant oldVariant, World world)
    {
        for (DamageControl damage : this.damage.values())
        {
            damage.addBlock(pos, oldVariant, world);
        }
    }
}