package mchorse.bbs.events.register;

import javax.script.ScriptEngine;

/**
 * Register scripting variables event. This event allows to register
 * variables that could be accessed in scripts.
 */
public class RegisterScriptVariables
{
    public final ScriptEngine engine;
    public final boolean isRepl;

    public RegisterScriptVariables(ScriptEngine engine, boolean isRepl)
    {
        this.engine = engine;
        this.isRepl = isRepl;
    }
}