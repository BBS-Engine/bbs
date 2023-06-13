package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.AbstractBlock;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.ui.UIKeys;

public abstract class TriggerBlock extends AbstractBlock
{
    public int frequency = 1;

    private int tick;

    @Override
    public String stringify()
    {
        return UIKeys.C_TRIGGER.get(BBS.getFactoryTriggers().getType(this)).get();
    }

    public void triggerWithFrequency(DataContext context)
    {
        this.tick += 1;

        if (this.tick > 0 && this.tick % Math.max(this.frequency, 1) == 0)
        {
            this.trigger(context);
            this.tick = 0;
        }
    }

    public abstract void trigger(DataContext context);

    public abstract boolean isEmpty();

    @Override
    public void toData(MapType data)
    {
        data.putInt("frequency", this.frequency);
    }

    @Override
    public void fromData(MapType data)
    {
        this.frequency = data.getInt("frequency");
    }
}