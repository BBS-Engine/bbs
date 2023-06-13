package mchorse.bbs.game.events.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.events.EventContext;

public class SwitchNode extends EventBaseNode
{
    public String expression = "";

    public SwitchNode()
    {}

    @Override
    protected String getDisplayTitle()
    {
        return this.expression;
    }

    @Override
    public int execute(EventContext context)
    {
        Object value = null;

        try
        {
            if (!this.expression.isEmpty())
            {
                value = BBSData.getScripts().repl(this.expression).output;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (value != null)
        {
            return 1 + ((Number) value).intValue();
        }

        return this.booleanToExecutionCode(false);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (!this.expression.isEmpty())
        {
            data.putString("expression", this.expression);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("expression"))
        {
            this.expression = data.getString("expression");
        }
    }
}