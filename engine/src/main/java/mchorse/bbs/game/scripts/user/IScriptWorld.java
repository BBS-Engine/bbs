package mchorse.bbs.game.scripts.user;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.game.states.States;
import mchorse.bbs.world.World;
import mchorse.bbs.world.objects.WorldObject;

import java.util.List;

/**
 * This interface represent a world passed in the event.
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var world = bbs.worlds.getCurrent();
 *
 *        // Do something with world...
 *    }
 * }</pre>
 */
public interface IScriptWorld
{
    /**
     * Get raw world instance.
     */
    public World getRawWorld();

    /**
     * Set a block at XYZ.
     *
     * <pre>{@code
     *    // TODO: ...
     * }</pre>
     */
    public void setBlock(int x, int y, int z, IScriptBlockVariant variant);

    /**
     * Get block state at given XYZ.
     *
     * <pre>{@code
     *    // TODO: ...
     * }</pre>
     *
     * @return a block state at given XYZ, or null if the chunk isn't loaded
     */
    public IScriptBlockVariant getBlock(int x, int y, int z);

    /* Ray tracing */

    /**
     * Ray trace from given point in given direction.
     *
     * <p>Check {@link IScriptRayTrace} for an example.</p>
     */
    public IScriptRayTrace rayTrace(double x, double y, double z, float dx, float dy, float dz, double maxDistance);

    /**
     * Ray trace from entity's looking direction (excluding entities).
     *
     * <p>Check {@link IScriptRayTrace} for an example.</p>
     */
    public IScriptRayTrace rayTraceBlock(double x, double y, double z, float dx, float dy, float dz, double maxDistance);

    /* World objects */

    /**
     * Get a world object by given ID.
     *
     * @param id ID of the object.
     * @return First world object found by given ID.
     */
    public WorldObject getObject(String id);

    /**
     * Get world objects by given ID.
     *
     * @param id ID of the object.
     * @return All world object found by given ID.
     */
    public List<WorldObject> getObjects(String id);

    /**
     * Get all world objects.
     */
    public List<WorldObject> getAllObjects();

    /* Spawn entities */

    /**
     * Spawn an entity at given position.
     *
     * <pre>{@code
     *    // TODO: ...
     * }</pre>
     */
    public default IScriptEntity spawnEntity(String id, double x, double y, double z)
    {
        return this.spawnEntity(id, x, y, z, null);
    }

    /**
     * Spawn an entity at given position with additional data.
     *
     * <pre>{@code
     *    // TODO: ...
     * }</pre>
     */
    public IScriptEntity spawnEntity(String id, double x, double y, double z, MapType data);

    /**
     * Spawn an item stack as an entity at given XYZ.
     *
     * <pre>{@code
     *    var item = bbs.items.create("bbs@hamster", 64);
     *    var world = bbs.worlds.getCurrent();
     *    var e = world.dropItem(item, 0, 4, 0);
     *
     *    e.setVelocity(Math.random() - 0.5, 1, Math.random() - 0.5);
     * }</pre>
     */
    public IScriptEntity dropItem(ItemStack stack, double x, double y, double z);

    /**
     * Get entities within the box specified by given coordinates in this world.
     * This method limits to scanning entities only within <b>100 blocks</b>
     * in any direction. If the box provided has any of its sizes that is longer
     * than 100 blocks, then it will simply return an empty list.
     *
     * <pre>{@code
     *    // Throw every entity within given AABB (box) in the air!
     *    var world = bbs.worlds.getCurrent();
     *    var entities = world.getEntities(-5, -5, -5, 5, 5, 5);
     *
     *    for (var i in entities)
     *    {
     *        var e = entities[i];
     *
     *        e.setVelocity(Math.random() * 0.5 - 0.25, 1, Math.random() * 0.5 - 0.25);
     *    }
     * }</pre>
     */
    public List<IScriptEntity> getEntities(double x1, double y1, double z1, double x2, double y2, double z2);

    /**
     * Get entities within the sphere specified by given coordinates and radius in
     * this world. This method limits to scanning entities only within <b>50 blocks
     * radius</b> in any direction. If the sphere provided has the radius that is
     * longer than 100 blocks, then it will simply return an empty list.
     *
     * <pre>{@code
     *    // Throw every entity within given sphere in the air!
     *    var world = bbs.worlds.getCurrent();
     *    var entities = world.getEntities(0, 0, 0, 5);
     *
     *    for (var i in entities)
     *    {
     *        var e = entities[i];
     *
     *        e.setVelocity(Math.random() * 0.5 - 0.25, 1, Math.random() * 0.5 - 0.25);
     *    }
     * }</pre>
     */
    public List<IScriptEntity> getEntities(double x, double y, double z, double radius);

    /**
     * Get an entity in the world by its UUID.
     */
    public IScriptEntity getEntityByUUID(String uuid);

    /**
     * Get all entities.
     *
     * <pre>{@code
     *    // Throw every entity in the world in the air!
     *    var world = bbs.worlds.getCurrent();
     *    var entities = world.getAllEntities();
     *
     *    for (var i in entities)
     *    {
     *        var e = entities[i];
     *
     *        e.setVelocity(Math.random() * 0.5 - 0.25, 1, Math.random() * 0.5 - 0.25);
     *    }
     * }</pre>
     */
    public List<IScriptEntity> getAllEntities();

    /**
     * Play a sound event in the world.
     *
     * <p>For all possible sound event IDs, please refer to either <code>/playsound</code>
     * command, or script editor's sound picker.</p>
     *
     * <pre>{@code
     *    bbs.worlds.getCurrent().playSound("assets@sounds/click.ogg", 0, 4, 0);
     * }</pre>
     */
    public default void playSound(String event, double x, double y, double z)
    {
        this.playSound(event, x, y, z, 1F, 1F);
    }

    /**
     * Play a sound event in the world with volume and pitch.
     *
     * <pre>{@code
     *    bbs.worlds.getCurrent().playSound("assets@sounds/click.ogg", 0, 4, 0, 1, 0.9);
     * }</pre>
     */
    public void playSound(String event, double x, double y, double z, float volume, float pitch);

    /**
     * Get world's states.
     */
    public States getStates();

    /**
     * Display a world form in the world at given point.
     */
    public default void displayForm(Form form, int expiration, double x, double y, double z)
    {
        this.displayForm(form, expiration, x, y, z, 0, 0);
    }

    /**
     * Display a world form in the world at given point with rotation.
     *
     * <pre>{@code
     *    // TODO: ...
     * }</pre>
     *
     * @param form Form that will be displayed (if <code>null</code>, then it won't send anything).
     * @param expiration For how many ticks will this displayed form exist on the client side.
     * @param yaw Horizontal rotation in degrees.
     * @param pitch Vertical rotation in degrees.
     */
    public void displayForm(Form form, int expiration, double x, double y, double z, float yaw, float pitch);
}