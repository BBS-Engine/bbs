package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.ui.UIKeys;

public class ConditionConditionBlock extends ConditionBlock
{
    public Condition condition = new Condition(false);

    @Override
    public boolean evaluateBlock(DataContext context)
    {
        return this.condition.execute(context);
    }

    @Override
    public String stringify()
    {
        return UIKeys.CONDITIONS_CONDITION_STRING.formatString(this.condition.blocks.size());
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

        this.condition.fromData(data.getMap("condition"));
    }
}