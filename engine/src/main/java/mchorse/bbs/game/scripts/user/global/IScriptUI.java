package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.game.scripts.user.IScriptEvent;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.game.scripts.user.ui.IScriptUIContext;

/**
 * Script UI API.
 *
 * <p>You can access it in the script as <code>bbs.ui</code> or <code>bbs.getUi()</code>.
 */
public interface IScriptUI
{
    /**
     * Create a UI from user created UI in the UI dashboard panel (with no script handler).
     */
    public default IScriptUIBuilder createFromData(String id)
    {
        return this.createFromData(id, "", "");
    }

    /**
     * Create a UI from user created UI in the UI dashboard panel with this attached
     * handler script to it (and a different function).
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.createFromData("ui", c, "handler");
     *
     *        // Populate default values or add more elements
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var data = bbs.ui.getUIContext();
     *
     *        print(data.getData());
     *    }
     * }</pre>
     */
    public default IScriptUIBuilder createFromData(String id, IScriptEvent event, String function)
    {
        return this.createFromData(id, event.getScript(), function);
    }

    /**
     * Create a UI from user created UI in the UI dashboard panel with attached
     * handler script to it (and a function).
     */
    public IScriptUIBuilder createFromData(String id, String script, String function);

    /**
     * Create a UI. You can send it to the player by using
     * {@link #open(IScriptUIBuilder)} method.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var label = ui.label("Hello, world!").background(0x88000000);
     *
     *        label.rxy(0.5, 0.5).wh(80, 20).anchor(0.5).labelAnchor(0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     */
    public default IScriptUIBuilder create()
    {
        return this.create("", "");
    }

    /**
     * Create a UI with a script handler. You can send it to the
     * player by using {@link #open(IScriptUIBuilder)} method.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var label = ui.label("Hello, world!").background(0x88000000);
     *        var button = ui.button("Push me!").id("button");
     *
     *        label.rxy(0.5, 0.5).wh(80, 20).anchor(0.5).labelAnchor(0.5);
     *        label.rx(0.5).ry(0.5, 25).wh(80, 20).anchor(0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        if (uiContext.getLast() === "button")
     *        {
     *            // Button was pressed
     *        }
     *    }
     * }</pre>
     *
     * @param event Script event (whose script ID will be used for UI's user input handler).
     * @param function Given script's function that will be used as UI's user input handler.
     */
    public default IScriptUIBuilder create(IScriptEvent event, String function)
    {
        return this.create(event.getScript(), function);
    }

    /**
     * Create a UI with a script handler. You can send it to the
     * player by using {@link #open(IScriptUIBuilder)} method.
     *
     * <p>Script and function arguments allow to point to the function in some
     * script, which it will be responsible for handling the user input from
     * scripted UI.</p>
     *
     * <p>In the UI handler, you can access subject's UI context ({@link IScriptUIContext})
     * which has all the necessary methods to handle user's input.</p>
     *
     * <pre>{@code
     *    // ui.js
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create("handler", "main").background();
     *        var label = ui.label("Hello, world!").background(0x88000000);
     *        var button = ui.button("Push me!").id("button");
     *
     *        label.rxy(0.5, 0.5).wh(80, 20).anchor(0.5).labelAnchor(0.5);
     *        label.rx(0.5).ry(0.5, 25).wh(80, 20).anchor(0.5);
     *
     *        bbs.ui.open(ui);
     *    }
     *
     *    // handler.js
     *    function main(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *
     *        if (uiContext.getLast() === "button")
     *        {
     *            // Button was pressed
     *        }
     *    }
     * }</pre>
     *
     * @param script The script which will be used as UI's user input handler.
     * @param function Given script's function that will be used as UI's user input handler.
     */
    public IScriptUIBuilder create(String script, String function);

    /**
     * Open UI for this player.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var button = ui.button("Push me").id("button");
     *
     *        // Place a button in the middle of the screen
     *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     */
    public default void open(IScriptUIBuilder builder)
    {
        this.open(builder, false);
    }

    /**
     * Open UI for this player with default data populated.
     *
     * <p>By default, default data population is disabled, meaning that
     * once the UI was opened, UI context's data will be empty. By enabling
     * default data population, UI context's data gets filled with all
     * component's default data.</p>
     *
     * <p>This is useful when you need to data to be present in the handler
     * at start, so you wouldn't need to do extra checks.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var button = ui.button("Push me").id("button");
     *        var name = ui.textbox("John").id("name");
     *        var lastname = ui.textbox("Smith").id("lastname");
     *
     *        // Place a button in the middle of the screen
     *        button.rxy(0.5, 0.5).wh(80, 20).anchor(0.5);
     *        name.rx(0.5).ry(0.5, 25).wh(80, 20).anchor(0.5);
     *        lastname.rx(0.5).ry(0.5, 50).wh(80, 20).anchor(0.5);
     *
     *        // Open the UI with default data populated
     *        bbs.ui.open(ui, true);
     *    }
     *
     *    function handler(c)
     *    {
     *        var uiContext = bbs.ui.getUIContext();
     *        var data = uiContext.getData();
     *
     *        // If false was passed into openUI as second argument
     *        // Then name or last name wouldn't be immediately populated
     *        // as John Smith
     *        bbs.send("Your name is: " + data.getString("name") + " " + data.getString("lastname"));
     *    }
     * }</pre>
     */
    public boolean open(IScriptUIBuilder builder, boolean defaultData);

    /**
     * Close currently opened user interface.
     */
    public void close();

    /**
     * Get the UI context of currently opened user UI. See {@link IScriptUIContext}
     * for code examples.
     */
    public IScriptUIContext getUIContext();
}