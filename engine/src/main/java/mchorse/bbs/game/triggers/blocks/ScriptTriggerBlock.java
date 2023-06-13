package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.utils.DataContext;

public class ScriptTriggerBlock extends DataTriggerBlock
{
    public String function = "";
    public boolean inline;
    public String code = "";

    public ScriptTriggerBlock()
    {
        super();
    }

    @Override
    public String stringify()
    {
        if (!this.id.isEmpty() && !this.function.isEmpty())
        {
            return this.id + " (" + this.function + ")";
        }

        return super.stringify();
    }

    @Override
    public void trigger(DataContext context)
    {
        try
        {
            DataContext data = this.apply(context);

            if (this.inline)
            {
                BBSData.getScripts().repl(this.code, data);
            }
            else if (!this.id.isEmpty())
            {
                BBSData.getScripts().execute(this.id, this.function.trim(), data);

                if (!context.isCanceled())
                {
                    context.cancel(data.isCanceled());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected String getKey()
    {
        return "script";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("function", this.function);
        data.putBool("inline", this.inline);
        data.putString("code", this.code);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.function = data.getString("function");
        this.inline = data.getBool("inline");
        this.code = data.getString("code");
    }
}