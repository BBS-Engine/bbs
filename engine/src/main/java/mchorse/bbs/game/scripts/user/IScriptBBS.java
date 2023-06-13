package mchorse.bbs.game.scripts.user;

import mchorse.bbs.game.scripts.user.global.IScriptAnimations;
import mchorse.bbs.game.scripts.user.global.IScriptCamera;
import mchorse.bbs.game.scripts.user.global.IScriptClient;
import mchorse.bbs.game.scripts.user.global.IScriptData;
import mchorse.bbs.game.scripts.user.global.IScriptForms;
import mchorse.bbs.game.scripts.user.global.IScriptHUD;
import mchorse.bbs.game.scripts.user.global.IScriptItems;
import mchorse.bbs.game.scripts.user.global.IScriptUI;
import mchorse.bbs.game.scripts.user.global.IScriptWorlds;

/**
 * BBS scripting API which offers sub APIs.
 *
 * <p>You can access it in the script as <code>bbs</code> global variable.
 */
public interface IScriptBBS
{
    /* Sub APIs */

    /**
     * Get animations sub API.
     */
    public IScriptAnimations getAnimations();

    /**
     * Get client sub API.
     */
    public IScriptClient getClient();

    /**
     * Get items sub API.
     */
    public IScriptItems getItems();

    /**
     * Get data sub API.
     */
    public IScriptData getData();

    /**
     * Get forms sub API.
     */
    public IScriptForms getForms();

    /**
     * Get UI sub API.
     */
    public IScriptUI getUi();

    /**
     * Get camera sub API.
     */
    public IScriptCamera getCamera();

    /**
     * Get HUD scenes sub API.
     */
    public IScriptHUD getHud();

    /**
     * Get worlds sub API.
     */
    public IScriptWorlds getWorlds();

    /* Factories */

    public IScriptBlockVariant getBlockVariant(String blockId, int variant);

    /**
     * Check whether the user is currently in development mode.
     */
    public boolean isDevelopment();

    /**
     * Get a global arbitrary object.
     *
     * <pre>{@code
     *    var number = bbs.get("number");
     *
     *    if (number === null || number === undefined)
     *    {
     *        number = 42;
     *        bbs.set("number", number);
     *    }
     * }</pre>
     */
    public Object get(String key);

    /**
     * Set a global arbitrary object during game's existence (other scripts
     * can access this data too).
     *
     * <pre>{@code
     *    var number = bbs.get("number");
     *
     *    if (number === null || number === undefined)
     *    {
     *        number = 42;
     *        bbs.set("number", number);
     *    }
     * }</pre>
     */
    public void set(String key, Object object);

    /**
     * Send a message to all players in the chat.
     *
     * <pre>{@code
     *    bbs.send("Hi :)");
     * }</pre>
     */
    public void send(String message);

    /**
     * Dump the simple representation of given non-JS object into the string (to see
     * what fields and methods are available for use).
     *
     * <pre>{@code
     *    bbs.send(bbs.dump(c.getSubject()));
     * }</pre>
     */
    public default String dump(Object object)
    {
        return this.dump(object, true);
    }

    /**
     * Dump given non-JS object into the string (to see what fields and methods are
     * available for use).
     *
     * <pre>{@code
     *    bbs.send(bbs.dump(c.getSubject(), true));
     * }</pre>
     *
     * @param simple Whether you want to see simple or full information about
     *               the object.
     */
    public String dump(Object object, boolean simple);
}