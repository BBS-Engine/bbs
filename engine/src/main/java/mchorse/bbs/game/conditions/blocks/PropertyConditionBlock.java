package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.Comparison;

public abstract class PropertyConditionBlock extends TargetConditionBlock
{
    public Comparison comparison = new Comparison();

    /**
     * Compare given value to expression or comparison mode
     */
    protected boolean compare(double a)
    {
        return this.comparison.compare(a);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.combine(this.comparison.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.comparison.fromData(data);
    }
}
