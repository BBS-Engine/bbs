package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract entity modifier
 * 
 * Abstract class for any new modifiers which are going to use entity 
 * selector to fetch an entity and apply some modifications to the path 
 * based on the entity.
 */
public abstract class EntityClip extends CameraClip
{
    /**
     * Position which may be used for calculation of relative
     * camera fixture animations
     */
    public Position position = new Position(0, 0, 0, 0, 0);

    /**
     * Target entity
     */
    public List<Entity> entities;

    /**
     * Target (entity) selector
     *
     * @link https://minecraft.gamepedia.com/Commands#Target_selector_variables
     */
    public final ValueString selector = new ValueString("selector", "");
    public final ValuePoint offset = new ValuePoint("offset", new Point(0, 0, 0));

    public EntityClip()
    {
        super();

        this.register(this.selector);
        this.register(this.offset);
    }

    /**
     * Try finding entity based on entity selector or target's UUID
     */
    public void tryFindingEntity(World world)
    {
        this.entities = null;

        this.tryFindingEntityClient(world, this.selector.get());
    }

    /**
     * Fancier targeting mechanism
     */
    private void tryFindingEntityClient(World world, String selector)
    {
        selector = selector.trim();

        List<Entity> entities = new ArrayList<Entity>();

        for (Entity entity : world.entities)
        {
            if (entity.id.equals(selector))
            {
                entities.add(entity);
            }
        }

        this.entities = entities.isEmpty() ? null : entities;
    }

    /**
     * Check for dead entities
     */
    protected boolean checkForDead()
    {
        if (this.entities == null)
        {
            return true;
        }

        Iterator<Entity> it = this.entities.iterator();

        while (it.hasNext())
        {
            Entity entity = it.next();

            if (entity.isRemoved())
            {
                it.remove();
            }
        }

        if (this.entities.isEmpty())
        {
            this.entities = null;
        }

        return this.entities == null;
    }
}