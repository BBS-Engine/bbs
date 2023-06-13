package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

/**
 * Script HUD stages API.
 *
 * <p>You can access it in the script as <code>bbs.hud</code> or <code>bbs.getHud()</code>.
 */
public interface IScriptHUD
{
    /**
     * Setup (initiate) an HUD scene for this player.
     *
     * <pre>{@code
     *    bbs.hud.setup("test");
     * }</pre>
     *
     * @param id HUD scene's ID/filename.
     */
    public boolean setup(String id);

    /**
     * Change a form in a HUD scene at given index with given form.
     *
     * <pre>{@code
     *    bbs.hud.changeForm("test", 0, bbs.forms.create("{id:\"bbs:model\",model:\"normie\"}"));
     * }</pre>
     *
     * @param id HUD scene's ID/filename.
     * @param index Index of the form in the scene that should be changed (0 is the first, and so on).
     */
    public void changeForm(String id, int index, Form form);

    /**
     * Change a form in a HUD scene at given index with a form described by given data.
     *
     * <pre>{@code
     *    bbs.hud.changeForm("test", 0, bbs.data.map("{id:\"bbs:model\",model:\"normie\"}"));
     * }</pre>
     *
     * @param id HUD scene's ID/filename.
     * @param index Index of the form in the scene that should be changed (0 is the first, and so on).
     * @param form Data of the form.
     */
    public void changeForm(String id, int index, MapType form);

    /**
     * Close all HUD scenes.
     *
     * <pre>{@code
     *    bbs.hud.closeAll();
     * }</pre>
     */
    public default void closeAll()
    {
        this.close(null);
    }

    /**
     * Close specific HUD scene.
     *
     * <pre>{@code
     *    bbs.hud.close("test");
     * }</pre>
     *
     * @param id HUD scene's ID/filename.
     */
    public void close(String id);
}