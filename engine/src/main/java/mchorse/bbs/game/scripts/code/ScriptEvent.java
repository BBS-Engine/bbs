package mchorse.bbs.game.scripts.code;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.scripts.ScriptExecutionFork;
import mchorse.bbs.game.scripts.code.entities.ScriptEntity;
import mchorse.bbs.game.scripts.user.IScriptEvent;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.game.scripts.user.entities.IScriptPlayer;
import mchorse.bbs.game.utils.DataContext;

import java.util.Map;

public class ScriptEvent implements IScriptEvent
{
    private DataContext context;
    private String script;
    private String function;

    private IScriptEntity subject;
    private IScriptEntity object;

    public ScriptEvent(DataContext context, String script, String function)
    {
        this.context = context;
        this.script = script;
        this.function = function;
    }

    @Override
    public String getScript()
    {
        return this.script == null ? "" : this.script;
    }

    @Override
    public String getFunction()
    {
        return this.function == null ? "" : this.function;
    }

    @Override
    public IScriptEntity getSubject()
    {
        if (this.subject == null && this.context.subject != null)
        {
            this.subject = ScriptEntity.create(this.context.subject);
        }

        return this.subject;
    }

    @Override
    public IScriptEntity getObject()
    {
        if (this.object == null && this.context.object != null)
        {
            this.object = ScriptEntity.create(this.context.object);
        }

        return this.object;
    }

    @Override
    public IScriptPlayer getPlayer()
    {
        IScriptEntity subject = this.getSubject();
        IScriptEntity object = this.getObject();

        if (subject instanceof IScriptPlayer)
        {
            return (IScriptPlayer) subject;
        }
        else if (object instanceof IScriptPlayer)
        {
            return (IScriptPlayer) object;
        }

        return null;
    }

    @Override
    public Map<String, Object> getValues()
    {
        return this.context.getValues();
    }

    @Override
    public Object getValue(String key)
    {
        return this.context.getValue(key);
    }

    @Override
    public void setValue(String key, Object value)
    {
        this.context.getValues().put(key, value);
    }

    /* Useful methods */

    @Override
    public void cancel()
    {
        this.context.cancel();
    }

    @Override
    public void scheduleScript(int delay)
    {
        this.scheduleScript(this.function, delay);
    }

    @Override
    public void scheduleScript(String function, int delay)
    {
        this.scheduleScript(this.script, function, delay);
    }

    @Override
    public void scheduleScript(String script, String function, int delay)
    {
        this.context.world.bridge.get(IBridgeWorld.class).getWorld().executables.add(new ScriptExecutionFork(this.context.copy(), script, function, delay));
    }

    @Override
    public void scheduleScript(int delay, ScriptObjectMirror function)
    {
        if (function != null && function.isFunction())
        {
            this.context.world.bridge.get(IBridgeWorld.class).getWorld().executables.add(new ScriptExecutionFork(this.context.copy(), function, delay));
        }
        else
        {
            throw new IllegalStateException("Given object is null in script " + this.script + " (" + this.function + " function)!");
        }
    }
}