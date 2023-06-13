package mchorse.bbs.game.triggers;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.triggers.blocks.TriggerBlock;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Trigger implements IMapSerializable
{
    public final List<TriggerBlock> blocks = new ArrayList<TriggerBlock>();

    private boolean empty;

    public void copy(Trigger trigger)
    {
        this.blocks.clear();

        for (TriggerBlock block : trigger.blocks)
        {
            Link type = BBS.getFactoryTriggers().getType(block);
            TriggerBlock newBlock = BBS.getFactoryTriggers().create(type);

            newBlock.fromData(block.toData());

            this.blocks.add(newBlock);
        }

        this.recalculateEmpty();
    }

    public void recalculateEmpty()
    {
        this.empty = true;

        for (TriggerBlock block : this.blocks)
        {
            if (!block.isEmpty())
            {
                this.empty = false;
            }
        }
    }

    public void trigger(Entity target)
    {
        this.trigger(new DataContext(target));
    }

    public void trigger(Entity target, Entity entity)
    {
        this.trigger(new DataContext(target, entity));
    }

    public void trigger(DataContext context)
    {
        for (TriggerBlock block : this.blocks)
        {
            if (context.isCanceled())
            {
                return;
            }

            block.triggerWithFrequency(context);
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType blocks = new ListType();

        for (TriggerBlock block : this.blocks)
        {
            blocks.add(BBS.getFactoryTriggers().toData(block));
        }

        data.put("blocks", blocks);
    }

    @Override
    public void fromData(MapType data)
    {
        this.blocks.clear();

        if (data.has("blocks"))
        {
            ListType blocks = data.getList("blocks");

            for (int i = 0; i < blocks.size(); i++)
            {
                TriggerBlock block = BBS.getFactoryTriggers().fromData(blocks.getMap(i));

                if (block != null)
                {
                    this.blocks.add(block);
                }
            }
        }

        this.recalculateEmpty();
    }

    public boolean isEmpty()
    {
        return this.empty;
    }
}