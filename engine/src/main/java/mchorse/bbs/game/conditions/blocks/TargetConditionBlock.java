package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.Target;
import mchorse.bbs.game.utils.TargetMode;

public abstract class TargetConditionBlock extends ConditionBlock
{
    public String id = "";
    public Target target = new Target(this.getDefaultTarget());

    protected TargetMode getDefaultTarget()
    {
        return TargetMode.GLOBAL;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("id", this.id.trim());
        data.combine(this.target.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.id = data.getString("id");
        this.target.fromData(data);
    }
}