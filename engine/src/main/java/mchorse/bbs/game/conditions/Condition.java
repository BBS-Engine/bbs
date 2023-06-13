package mchorse.bbs.game.conditions;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.blocks.ConditionBlock;
import mchorse.bbs.game.utils.DataContext;

import java.util.ArrayList;
import java.util.List;

public class Condition implements IMapSerializable
{
    public final List<ConditionBlock> blocks = new ArrayList<ConditionBlock>();

    private boolean defaultValue;

    public Condition()
    {
        this(false);
    }

    public Condition(boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public boolean execute(DataContext context)
    {
        if (this.blocks.isEmpty())
        {
            return this.defaultValue;
        }

        boolean result = this.blocks.get(0).evaluate(context);

        for (int i = 1; i < this.blocks.size(); i++)
        {
            ConditionBlock block = this.blocks.get(i);
            boolean value = block.evaluate(context);

            if (block.or)
            {
                result = result || value;
            }
            else
            {
                result = result && value;
            }
        }

        return result;
    }

    @Override
    public void toData(MapType data)
    {
        ListType blocks = new ListType();

        for (ConditionBlock block : this.blocks)
        {
            blocks.add(BBS.getFactoryConditions().toData(block));
        }

        if (blocks.size() > 0)
        {
            data.put("blocks", blocks);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        ListType blocks = data.getList("blocks");

        this.blocks.clear();

        for (int i = 0; i < blocks.size(); i++)
        {
            ConditionBlock block = BBS.getFactoryConditions().fromData(blocks.getMap(i));

            if (block != null)
            {
                this.blocks.add(block);
            }
        }
    }
}