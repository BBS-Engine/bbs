package mchorse.bbs.recording.actions;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.world.entities.Entity;

/**
 * Parent of all recording actions
 *
 * This class holds additional information about player's actions performed during
 * recording. Supports abstraction and stuffz.
 */
public abstract class Action implements IMapSerializable
{
    /**
     * Apply action on an actor (shoot arrow, mount entity, break block, etc.)
     *
     * Some action doesn't necessarily should have apply method (that's why this
     * method is empty)
     */
    public void apply(Entity actor)
    {}

    /**
     * Whether this action is safe. Safe action means that it doesn't 
     * modify the world, at max, only its user. 
     */
    public boolean isSafe()
    {
        return false;
    }
}