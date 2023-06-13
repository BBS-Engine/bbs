package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.game.scripts.user.IScriptWorld;

/**
 * Script worlds API.
 *
 * <p>You can access it in the script as <code>bbs.worlds</code> or <code>bbs.getWorlds()</code>.
 */
public interface IScriptWorlds
{
    /**
     * Load another world.
     *
     * <pre>{@code
     *    bbs.worlds.load("test");
     * }</pre>
     */
    public boolean load(String world);

    /**
     * Load another world and place the player at given coordinates in the world.
     *
     * <pre>{@code
     *    bbs.worlds.loadAt("test", 146, 15, -353);
     * }</pre>
     */
    public default boolean loadAt(String world, double x, double y, double z)
    {
        return this.loadAt(world, x, y, z, 0, 0);
    }

    /**
     * Load another world and place the player at given coordinates, and with given rotations,
     * in the world.
     *
     * <pre>{@code
     *    bbs.worlds.loadAt("test", 146, 15, -353, 0, 90);
     * }</pre>
     */
    public boolean loadAt(String world, double x, double y, double z, float pitch, float yaw);

    /**
     * Get currently loaded world.
     *
     * <pre>{@code
     *    var world = bbs.worlds.getCurrent();
     *
     *    // Do soemthing with the world, see IScriptWorld for examples
     * }</pre>
     */
    public IScriptWorld getCurrent();
}