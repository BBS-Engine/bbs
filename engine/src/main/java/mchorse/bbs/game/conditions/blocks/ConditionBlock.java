package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.AbstractBlock;
import mchorse.bbs.game.utils.DataContext;

public abstract class ConditionBlock extends AbstractBlock
{
    public boolean not;
    public boolean or;

    public boolean evaluate(DataContext context)
    {
        boolean result = this.evaluateBlock(context);

        return this.not != result;
    }

    protected abstract boolean evaluateBlock(DataContext context);

    @Override
    public void toData(MapType data)
    {
        data.putBool("not", this.not);
        data.putBool("or", this.or);
    }

    @Override
    public void fromData(MapType data)
    {
        this.not = data.getBool("not");
        this.or = data.getBool("or");
    }
}