package mchorse.bbs.game.scripts.user.entities;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.user.IScriptRayTrace;
import mchorse.bbs.game.states.States;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * Entity interface.
 *
 * <p>This interface represents an entity, it could be a player, NPC,
 * or any other entity. <b>IMPORTANT</b>: any method that marks an argument or return
 * as of {@link IScriptEntity} type can return also {@link IScriptPlayer} if it's an
 * actual player!</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        if (c.getSubject().isPlayer())
 *        {
 *            // Do something with the player...
 *        }
 *        if (c.getSubject().isNpc())
 *        {
 *            // Do something with the NPC...
 *        }
 *        else
 *        {
 *            // Do something with the entity...
 *        }
 *    }
 * }</pre>
 */
public interface IScriptEntity
{
    /**
     * Get raw entity instance.
     */
    public Entity getRawEntity();

    /* Entity properties */

    /**
     * Get entity's position.
     *
     * <pre>{@code
     *    var pos = c.getSubject().getPosition();
     *
     *    c.send(c.getSubject().getName() + "'s position is (" + pos.x + ", " + pos.y + ", " + pos.z + ")");
     * }</pre>
     */
    public Vector3d getPosition();

    /**
     * Set entity's position (teleport).
     *
     * <pre>{@code
     *    c.getSubject().setPosition(800, 8, -135);
     * }</pre>
     */
    public void setPosition(double x, double y, double z);

    /**
     * Get entity's velocity.
     *
     * <pre>{@code
     *    var velocity = c.getSubject().getVelocity();
     *
     *    c.send(c.getSubject().getName() + "'s velocity is (" + velocity.x + ", " + velocity.y + ", " + velocity.z + ")");
     * }</pre>
     */
    public Vector3f getVelocity();

    /**
     * Set entity's velocity.
     *
     * <pre>{@code
     *    var velocity = c.getSubject().getVelocity();
     *
     *    if (velocity.y < 0)
     *    {
     *        // Reverse the falling motion into a jumping up motion
     *        c.getSubject().setVelocity(velocity.x, -velocity.y, velocity.z);
     *    }
     * }</pre>
     */
    public void setVelocity(float x, float y, float z);

    /**
     * Get entity's rotation (x is pitch, y is yaw, and z is yaw head, if entity
     * is living base).
     *
     * <pre>{@code
     *    var rotations = c.getSubject().getRotations();
     *    var pitch = rotations.x;
     *    var yaw = rotations.y;
     *    var yaw_head = rotations.z;
     *
     *    c.send(c.getSubject().getName() + "'s rotations are (" + pitch + ", " + yaw + ", " + yaw_head + ")");
     * }</pre>
     */
    public Vector3f getRotations();

    /**
     * Set entity's rotation.
     *
     * <pre>{@code
     *    // Make entity look at west
     *    c.getSubject().setRotations(0, 0, 0);
     * }</pre>
     */
    public void setRotations(float pitch, float yaw, float yawHead);

    /**
     * Get a vector in which direction entity looks.
     *
     * <pre>{@code
     *    var look = c.getSubject().getLook();
     *
     *    c.getSubject().setMotion(look.x * 0.5, look.y * 0.5, look.z * 0.5);
     * }</pre>
     */
    public Vector3f getLook();

    /**
     * Get entity's current hitbox width (and depth, it's the same number).
     */
    public float getWidth();

    /**
     * Get entity's current hitbox height.
     */
    public float getHeight();

    /**
     * Is this entity is sneaking.
     *
     * <pre>{@code
     *    var subject = c.getSubject();
     *
     *    if (subject.isSneaking())
     *    {
     *        subject.send("You completed Simon's task!");
     *    }
     * }</pre>
     */
    public boolean isSneaking();

    /* Ray tracing */

    /**
     * Ray trace from entity's looking direction (including any entity intersection).
     * Check {@link IScriptRayTrace} for an example.
     */
    public IScriptRayTrace rayTrace(double maxDistance);

    /**
     * Ray trace from entity's looking direction (excluding entities).
     * Check {@link IScriptRayTrace} for an example.
     */
    public IScriptRayTrace rayTraceBlock(double maxDistance);

    /* Items */

    /**
     * Get item held in main hand.
     *
     * <pre>{@code
     *    // TODO: example
     * }</pre>
     */
    public ItemStack getMainItem();

    /**
     * Set item held in main hand.
     *
     * <pre>{@code
     *    // TODO: example
     * }</pre>
     */
    public void setMainItem(ItemStack stack);

    /**
     * Get item held in off hand.
     *
     * <pre>{@code
     *    // TODO: example
     * }</pre>
     */
    public ItemStack getOffItem();

    /**
     * Set item held in off hand.
     *
     * <pre>{@code
     *    // TODO: example
     * }</pre>
     */
    public void setOffItem(ItemStack stack);

    /* Entity meta */

    /**
     * Get unique ID of this entity, which can be used, if needed, in
     * commands as a target selector.
     */
    public String getUniqueId();

    /**
     * Get entity's ID.
     */
    public String getEntityId();

    /**
     * Get how many ticks did this entity existed.
     */
    public int getTicks();

    /**
     * Get entity name.
     */
    public String getName();

    /**
     * Get entity's full (copy of its) data.
     */
    public MapType getFullData();

    /**
     * Overwrite data of this entity. <b>WARNING</b>: use it only if you know
     * what are you doing as this method can corrupt entities.
     */
    public void setFullData(MapType data);

    /**
     * Check whether this entity is a player.
     */
    public boolean isPlayer();

    /**
     * Check whether this entity is an NPC.
     */
    public boolean isNpc();

    /**
     * Check whether this entity is same as given entity.
     */
    public boolean isSame(IScriptEntity entity);

    /**
     * Remove this entity from the server without any dead effects (essentially despawn).
     */
    public void remove();

    /**
     * Kill this entity from the server by inflicting lots of damage.
     */
    public void kill();

    /**
     * Get entity's states (if it has some, only players and NPCs have states).
     *
     * @return entity's states, or null if this entity doesn't have states.
     */
    public States getStates();

    /**
     * Returns this entity's form (if it's present). It's not a copy, but
     * a direct reference.
     */
    public Form getForm();

    /**
     * Set entity's form.
     *
     * <pre>{@code
     *    var form = bbs.forms.create("{id:\"bbs:model\",model:\"normie\"}");
     *
     *    // Assuming c.getSubject() is a player or an NPC
     *    c.getSubject().setForm(form);
     * }</pre>
     *
     * @return if entity's form was changed successfully.
     */
    public boolean setForm(Form form);

    /**
     * Returns this entity's first-person form (if it's present). It's not a copy,
     * but a direct reference.
     */
    public Form getFirstPersonForm();

    /**
     * Set entity's form.
     *
     * <pre>{@code
     *    var form = bbs.forms.create("{id:\"bbs:model\",model:\"normie_fp\"}");
     *
     *    // Assuming c.getSubject() is a player or an NPC
     *    c.getSubject().setFirstPersonForm(form);
     * }</pre>
     *
     * @return if entity's first person form was changed successfully.
     */
    public boolean setFirstPersonForm(Form form);
}