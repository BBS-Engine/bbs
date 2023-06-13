package mchorse.bbs.game.scripts.user;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.game.scripts.user.entities.IScriptPlayer;

import java.util.Map;

/**
 * Script event.
 *
 * <p>This interface represent the event, the only argument that was passed
 * into script's function. It contains many different useful methods to
 * interact with the game engine.</p>
 */
public interface IScriptEvent
{
    /**
     * Get script's ID to which this event was passed to.
     */
    public String getScript();

    /**
     * Get script's function name.
     */
    public String getFunction();

    /**
     * Get subject (primary) entity that was passed into the event.
     */
    public IScriptEntity getSubject();

    /**
     * Get object (secondary) entity that was passed into the event.
     */
    public IScriptEntity getObject();

    /**
     * Get the first player from either subject or object (or <code>null</code>, if there is no player).
     */
    public IScriptPlayer getPlayer();

    /**
     * Get a map of extra context values that was passed into the event.
     */
    public Map<String, Object> getValues();

    /**
     * Get a value for given key (might be a <code>null</code>).
     */
    public Object getValue(String key);

    /**
     * Set a value for given key in extra data.
     */
    public void setValue(String key, Object value);

    /* Useful methods */

    /**
     * Cancel the trigger event.
     */
    public void cancel();

    /**
     * Schedule execution of the same script (with same function)
     * given ticks forward.
     *
     * <p>Read {@link #scheduleScript(String, String, int)} for more information.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var states = c.getServer().getStates();
     *        var counter = states.getNumber("counter");
     *
     *        if (counter < 10)
     *        {
     *            c.send(counter + " Mississippi...");
     *            states.add("counter", 1);
     *
     *            c.scheduleScript(20);
     *        }
     *        else
     *        {
     *            states.remove("counter");
     *            c.send("Here I go!");
     *        }
     *    }
     * }</pre>
     *
     * @param delay How many ticks should pass before scheduled script will be executed.
     */
    public void scheduleScript(int delay);

    /**
     * Schedule execution of the same script with given function
     * given ticks forward.
     *
     * <p>Read {@link #scheduleScript(String, String, int)} for more information.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        // Schedule script execution of function other
     *        // within same script a second later
     *        c.scheduleScript("other", 20);
     *    }
     *
     *    function other(c)
     *    {
     *        c.send("A second ago, function \"main\" told me to say \"hi\" to you... :)")
     *    }
     * }</pre>
     */
    public void scheduleScript(String function, int delay);

    /**
     * Schedule execution of given script with specific function
     * given ticks forward.
     *
     * <p>When scheduling a script, it will use same data which were passed
     * into current script's function. I.e. subject, object, world, server
     * and values.</p>
     *
     * <p><b>ProTip</b>: if you put some values into this context using
     * {@link #setValue(String, Object)}, then that value will be also available
     * when the scheduled script will be executed.</p>
     *
     * <pre>{@code
     *    // Script "a"
     *    function main(c)
     *    {
     *        // As ProTip states, you can pass some value using
     *        // setValue() and getValue() event's functions
     *        c.setValue("message", "Hello!");
     *
     *        // Schedule script "b" execution a second later
     *        c.scheduleScript("b", "main", 20);
     *    }
     *
     *    // Script "b"
     *    function main(c)
     *    {
     *        c.send("A second ago, script \"a\" told me deliver this message: " + c.getValue("message"));
     *    }
     * }</pre>
     */
    public void scheduleScript(String script, String function, int delay);

    /**
     * Schedule a JavaScript function (instead of script). Once the timer has expired,
     * given function will be called with this context as the only argument.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        c.scheduleScript(60, function (context)
     *        {
     *            context.send("This was called three seconds later!");
     *        });
     *    }
     * }</pre>
     */
    public void scheduleScript(int delay, ScriptObjectMirror function);
}