package mchorse.bbs.game.scripts;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.scripts.code.ScriptEvent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.executables.IExecutable;

public class ScriptExecutionFork implements IExecutable
{
    public DataContext context;
    public ScriptObjectMirror object;
    public String script;
    public String function;
    public int timer;

    public ScriptExecutionFork(DataContext context, String script, String function, int timer)
    {
        this.context = context;
        this.script = script;
        this.function = function;
        this.timer = timer;
    }

    public ScriptExecutionFork(DataContext context, ScriptObjectMirror object, int timer)
    {
        this.context = context;
        this.object = object;
        this.timer = timer;
    }

    @Override
    public boolean update()
    {
        if (this.timer <= 0)
        {
            try
            {
                if (this.object != null)
                {
                    this.object.call(null, new ScriptEvent(this.context, null, null));
                }
                else
                {
                    BBSData.getScripts().execute(this.script, this.function, this.context);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return true;
        }

        this.timer -= 1;

        return false;
    }
}