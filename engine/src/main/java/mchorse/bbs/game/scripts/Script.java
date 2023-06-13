package mchorse.bbs.game.scripts;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.register.RegisterScriptVariables;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.code.ScriptEvent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.manager.data.AbstractData;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Script extends AbstractData
{
    public String code = "";
    public boolean unique = true;
    public List<String> libraries = new ArrayList<String>();

    private ScriptEngine engine;
    private List<ScriptRange> ranges;

    public Script()
    {}

    public void start(ScriptManager manager) throws ScriptException
    {
        if (this.engine == null)
        {
            this.engine = ScriptUtils.sanitize(ScriptUtils.tryCreatingEngine());
            this.engine.getContext().setAttribute("javax.script.filename", this.getId() + ".js", ScriptContext.ENGINE_SCOPE);

            StringBuilder finalCode = new StringBuilder();
            Set<String> alreadyLoaded = new HashSet<String>();
            int total = 0;

            for (String library : this.libraries)
            {
                /* Don't load this script as its own library nor load repeatedly
                 * same library twice or more */
                if (library.equals(this.getId()) || alreadyLoaded.contains(library))
                {
                    continue;
                }

                try
                {
                    File jsFile = manager.getJSFile(library);
                    String code = IOUtils.readText(jsFile);

                    finalCode.append(code);
                    finalCode.append("\n");

                    if (this.ranges == null)
                    {
                        this.ranges = new ArrayList<ScriptRange>();
                    }

                    this.ranges.add(new ScriptRange(total, library));

                    total += StringUtils.countMatches(code, "\n") + 1;
                }
                catch (Exception e)
                {
                    System.err.println("[Mappet] Script library " + library + ".js failed to load...");
                    e.printStackTrace();
                }

                alreadyLoaded.add(library);
            }

            finalCode.append(this.code);

            if (this.ranges != null)
            {
                this.ranges.add(new ScriptRange(total, this.getId()));
            }

            BBS.events.post(new RegisterScriptVariables(this.engine, false));

            this.engine.put("bbs", new ScriptBBS(manager.bridge));
            this.engine.eval(finalCode.toString());
        }
    }

    public Object execute(String function, DataContext context) throws ScriptException, NoSuchMethodException
    {
        if (function.isEmpty())
        {
            function = "main";
        }

        try
        {
            return ((Invocable) this.engine).invokeFunction(function, new ScriptEvent(context, this.getId(), function));
        }
        catch (ScriptException e)
        {
            ScriptException exception = processScriptException(e);

            throw exception == null ? e : exception;
        }
    }

    private ScriptException processScriptException(ScriptException e)
    {
        if (this.ranges == null)
        {
            return null;
        }

        ScriptRange range = null;

        for (int i = this.ranges.size() - 1; i >= 0; i--)
        {
            ScriptRange possibleRange = this.ranges.get(i);

            if (possibleRange.lineOffset <= e.getLineNumber() - 1)
            {
                range = possibleRange;

                break;
            }
        }

        if (range != null)
        {
            String message = e.getMessage();
            int lineNumber = e.getLineNumber() - range.lineOffset;

            message = message.replaceFirst(this.getId() + ".js", range.script + ".js (in " + this.getId() + ".js)");
            message = message.replaceFirst("at line number [\\d]+", "at line number " + lineNumber);

            return new ScriptException(message, range.script, lineNumber, e.getColumnNumber());
        }

        return null;
    }

    @Override
    public void toData(MapType data)
    {
        ListType libraries = new ListType();

        for (String library : this.libraries)
        {
            libraries.addString(library);
        }

        data.putBool("unique", this.unique);
        data.put("libraries", libraries);
        data.putString("code", this.code);
    }

    @Override
    public void fromData(MapType data)
    {
        this.unique = data.getBool("unique", this.unique);

        if (data.has("libraries"))
        {
            ListType libraries = data.getList("libraries");

            this.libraries.clear();

            for (int i = 0, c = libraries.size(); i < c; i++)
            {
                this.libraries.add(libraries.getString(i));
            }
        }

        this.code = data.getString("code");
    }
}