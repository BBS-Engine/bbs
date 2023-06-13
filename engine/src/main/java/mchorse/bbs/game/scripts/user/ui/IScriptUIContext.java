package mchorse.bbs.game.scripts.user.ui;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.user.global.IScriptUI;

/**
 * This interface represents an UI context, which is an object that
 * stores UI data when player has opened custom GUI made with
 * {@link IScriptUIBuilder}.
 *
 * <p>It's usually available after the UI has been sent using {@link IScriptUI#open(IScriptUIBuilder)}
 * in the handler function.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var button = ui.button("Push me").id("button");
 *
 *        // Place a button in the middle of the screen
 *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *
 *        // Do something with UI context...
 *    }
 * }</pre>
 */
public interface IScriptUIContext
{
    /**
     * Get the data map of the UI.
     *
     * <p>This data is formed by the UI components that have ID. The data will
     * be populated only when the UI elements will actually change their values.
     * Until then this data will be empty.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var name = ui.textbox().id("name");
     *        var lastname = ui.textbox().id("lastname");
     *
     *        // Place two text fields in the middle of the screen
     *        name.rxy(0.5, 0.5).wh(140, 20).anchor(0.5);
     *        lastname.rx(0.5).ry(0.5, 25).wh(140, 20).anchor(0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        // If the user will input first in the name text box, the data
     *        // will be just {name:"John"}. If the user will input first
     *        // the lastname, then the data will be {lastname:"Appleseed"}.
     *        // Once both fields will be edited, you'll get:
     *        // {name:"John",lastname:"Appleseed"}
     *        c.send(uiContext.getData().stringify());
     *    }
     * }</pre>
     */
    public MapType getData();

    /**
     * Returns whether the UI was just closed.
     *
     * <p>The return value is <code>true</code> only when the user closed the
     * screen, and the UI context is about to get cleared from the player.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var button = ui.button("Push me").id("button");
     *
     *        // Place a button in the middle of the screen
     *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        // Check if the user closed the screen
     *        if (uiContext.isClosed())
     *        {
     *            // Do something with the data
     *            bbs.send("Welcome back to the world!");
     *        }
     *    }
     * }</pre>
     */
    public boolean isClosed();

    /**
     * Get the ID of last edited UI component.
     *
     * <p>If there were multiple UI elements were changed (see {@link UIComponent#updateDelay(int)}),
     * then only the last one will be provided.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var title = ui.label("...").id("title");
     *        var name = ui.textbox().id("name");
     *        var lastname = ui.textbox().id("lastname");
     *
     *        // Place two text fields in the middle of the screen
     *        name.rxy(0.5, 0.5).wh(140, 20).anchor(0.5);
     *        lastname.rx(0.5).ry(0.5, 25).wh(140, 20).anchor(0.5);
     *        title.rx(0.5).y(20).wh(140, 20).anchorX(0.5);
     *        title.labelAnchor(0.5, 0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *        var last = uiContext.getLast();
     *
     *        // Depending on the last value, update the title accordingly
     *        if (last === "name")
     *        {
     *            uiContext.get("title").label("Name: " + uiContext.getData().getString("name"));
     *        }
     *        else if (last === "lastname")
     *        {
     *            uiContext.get("title").label("Last name: " + uiContext.getData().getString("lastname"));
     *        }
     *    }
     * }</pre>
     */
    public String getLast();

    /**
     * Get the ID of the last pressed hot key.
     *
     * <p>If no keybind was pressed, it will be an empty string (<code>""</code>).
     * See {@link UIComponent#keybind(int, String, String, boolean, boolean, boolean)} method
     * for an example.</p>
     */
    public String getHotkey();

    /**
     * Get the ID of the last context menu item.
     *
     * <p>If no context menu was activated, it will be an empty string (<code>""</code>).
     * See {@link UIComponent#context(String, String, String, int)} method for an example.</p>
     */
    public String getContext();

    /* Server side modification */

    /**
     * Returns a UI component by given ID or <code>null</code>. You can use this
     * in the handler script of the UI to change certain properties.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var button = ui.button("Push me").id("button");
     *
     *        // Place a button in the middle of the screen
     *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        if (uiContext.getLast() === "button")
     *        {
     *            uiContext.get("button").label("Push me harder!");
     *        }
     *    }
     * }</pre>
     */
    public UIComponent get(String id);

    /**
     * Sends UI changes to the player.
     *
     * <p>If you edited the UI context's component data using {@link IScriptUIContext#get(String)}
     * outside of the UI handler script (UI context, after executing the handler script, sends the
     * changed data afterwards automatically), you need to manually send the data to the player
     * using this function.</p>
     *
     * <pre>{@code
     *    // ui.js
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var button = ui.button("Push me").id("button");
     *
     *        // Place a button in the middle of the screen
     *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
     *        bbs.ui.open(ui);
     *    }
     *
     *    // other.js
     *    function main(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        // Assuming that UI context is still present (i.e. the UI is still open)
     *        if (uiContext)
     *        {
     *            uiContext.get("button").label("Too late!");
     *            uiContext.sendToPlayer();
     *        }
     *    }
     * }</pre>
     */
    public void sendToPlayer();
}