package mchorse.bbs.game.events.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.events.EventContext;

public class ConditionNode extends EventBaseNode
{
    public Condition condition = new Condition();

    public ConditionNode()
    {}

    @Override
    protected String getDisplayTitle()
    {
        return this.condition.blocks.size() + " conditions";
    }

    @Override
    public int execute(EventContext context)
    {
        boolean result = this.condition.execute(context.data);

        return this.booleanToExecutionCode(result);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("condition", this.condition.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("condition"))
        {
            this.condition.fromData(data.getMap("condition"));
        }
    }
}