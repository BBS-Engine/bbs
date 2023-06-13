package mchorse.bbs.game.scripts;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ScriptUtils
{
    private static ScriptEngineManager manager;

    /**
     * Tries to create a script engine
     */
    public static ScriptEngine tryCreatingEngine()
    {
        for (String name : Arrays.asList("nashorn", "Nashorn", "javascript", "JavaScript", "js", "JS", "ecmascript", "ECMAScript"))
        {
            ScriptEngine engine = getManager().getEngineByName(name);

            if (engine != null)
            {
                return engine;
            }
        }

        try
        {
            Class factoryClass = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
            Object factory = factoryClass.getConstructor().newInstance();
            Method getScriptEnging = factoryClass.getDeclaredMethod("getScriptEngine");

            return (ScriptEngine) getScriptEnging.invoke(factory);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static ScriptEngineManager getManager()
    {
        try
        {
            if (manager == null)
            {
                manager = new ScriptEngineManager();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return manager;
    }

    public static ScriptEngine sanitize(ScriptEngine engine)
    {
        /* Remove */
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("exit");
        bindings.remove("quit");

        return engine;
    }
}