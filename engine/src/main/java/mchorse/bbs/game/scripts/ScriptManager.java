package mchorse.bbs.game.scripts;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.register.RegisterScriptVariables;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.code.ScriptEvent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.manager.BaseManager;
import mchorse.bbs.utils.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ScriptManager extends BaseManager<Script>
{
    public final Map<String, Object> objects = new HashMap<String, Object>();
    public IBridge bridge;

    private Map<String, Script> uniqueScripts = new HashMap<String, Script>();
    private ScriptEngine repl;
    private ScriptEvent initialEvent;
    private String replOutput = "";

    public ScriptManager(File folder, IBridge bridge)
    {
        super(folder);

        this.bridge = bridge;
    }

    public ScriptResult repl(String code) throws ScriptException
    {
        return this.repl(code, null);
    }

    /**
     * Execute a REPL code that came from a player
     */
    public ScriptResult repl(String code, DataContext context) throws ScriptException
    {
        this.replOutput = "";

        if (this.repl == null)
        {
            this.repl = ScriptUtils.sanitize(ScriptUtils.tryCreatingEngine());
            this.repl.put("____manager____", this);
            this.repl.put("bbs", new ScriptBBS(this.bridge));

            this.initialEvent = new ScriptEvent(new DataContext(this.bridge.get(IBridgeWorld.class).getWorld()), "", "");

            this.repl.eval("var ___p___ = print; print = function(message) { ____manager____.replPrint(message); ___p___(message); };");
        }

        ScriptEvent event = context == null ? this.initialEvent : new ScriptEvent(context, "", "");

        this.repl.put("c", event);

        BBS.events.post(new RegisterScriptVariables(this.repl, true));

        Object object = this.repl.eval(code);

        return new ScriptResult(this.replOutput, object);
    }

    public void replPrint(Object object)
    {
        if (object == null)
        {
            object = "undefined";
        }

        this.replOutput += object.toString() + "\n";
    }

    /**
     * Execute given script
     */
    public Object execute(String id, String function, DataContext context) throws ScriptException, NoSuchMethodException
    {
        Script script = this.getScript(id);

        return script == null ? null : script.execute(function, context);
    }

    private Script getScript(String id) throws ScriptException
    {
        Script script = this.uniqueScripts.get(id);

        if (script == null)
        {
            script = this.load(id);

            if (script != null && script.unique)
            {
                this.uniqueScripts.put(id, script);
            }
        }

        if (script == null)
        {
            return null;
        }

        script.start(this);

        return script;
    }

    @Override
    protected Script createData(String id, MapType mapType)
    {
        Script script = new Script();

        if (mapType != null)
        {
            script.fromData(mapType);
        }

        return script;
    }

    /* Custom implementation of base manager to support .js files */

    @Override
    public Script load(String id)
    {
        Script script = super.load(id);
        File js = this.getJSFile(id);

        if (js != null && js.isFile())
        {
            try
            {
                String code = IOUtils.readText(js);

                if (script == null)
                {
                    script = new Script();
                    script.setId(id);
                }

                script.code = code.replaceAll("\t", "    ").replaceAll("\r", "");
            }
            catch (Exception e)
            {}
        }

        return script;
    }

    @Override
    public boolean save(String id, MapType data)
    {
        String code = data.getString("code");

        data.remove("code");

        boolean result = super.save(id, data);

        if (!code.trim().isEmpty())
        {
            try
            {
                IOUtils.writeText(this.getJSFile(id), code);

                result = true;
            }
            catch (Exception e)
            {}
        }

        if (result)
        {
            this.uniqueScripts.remove(id);
        }

        return result;
    }

    /* Custom implementation of folder manager to support .js files */

    @Override
    public boolean exists(String name)
    {
        File js = this.getJSFile(name);

        return super.exists(name) || (js != null && js.exists());
    }

    @Override
    public boolean rename(String from, String to)
    {
        File js = this.getJSFile(from);
        boolean result = super.rename(from, to);

        if (js != null && js.exists())
        {
            return js.renameTo(this.getJSFile(to)) || result;
        }

        return result;
    }

    @Override
    public boolean delete(String name)
    {
        boolean result = super.delete(name);
        File js = this.getJSFile(name);

        return (js != null && js.delete()) || result;
    }

    @Override
    protected boolean isData(File file)
    {
        return super.isData(file) || file.getName().endsWith(".js");
    }

    public File getJSFile(String id)
    {
        if (this.folder == null)
        {
            return null;
        }

        return new File(this.folder, id + ".js");
    }
}