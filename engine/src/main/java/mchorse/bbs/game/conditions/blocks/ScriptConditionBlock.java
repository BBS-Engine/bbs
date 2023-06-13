package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.utils.DataContext;

public class ScriptConditionBlock extends ConditionBlock
{
    public boolean inline;
    public String script = "";
    public String function = "";
    public String customData = "";
    public String code = "";

    @Override
    protected boolean evaluateBlock(DataContext context)
    {
        if (!this.customData.isEmpty())
        {
            context = context.copy().parse(this.customData);
        }

        if (this.inline)
        {
            try
            {
                return this.objectToBoolean(BBSData.getScripts().repl(this.code, context).output);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return false;
        }

        try
        {
            if (!this.script.isEmpty())
            {
                return this.objectToBoolean(BBSData.getScripts().execute(this.script, this.function, context));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private boolean objectToBoolean(Object object)
    {
        if (object instanceof Boolean)
        {
            return (Boolean) object;
        }
        else if (object instanceof Number)
        {
            return Math.abs(((Number) object).doubleValue()) != 0;
        }

        return false;
    }

    @Override
    public String stringify()
    {
        if (this.inline)
        {
            return "Inline code";
        }

        if (this.function.isEmpty())
        {
            return this.script;
        }

        return this.script + " (" + this.function + ")";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("script", this.script);
        data.putString("function", this.function);
        data.putString("customData", this.customData);
        data.putString("code", this.code);
        data.putBool("inline", this.inline);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.script = data.getString("script");
        this.function = data.getString("function");
        this.customData = data.getString("customData");
        this.code = data.getString("code");
        this.inline = data.getBool("inline");
    }
}
