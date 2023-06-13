package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.DataContext;

public abstract class DataTriggerBlock extends StringTriggerBlock
{
    public String customData = "";

    public DataTriggerBlock()
    {
        super();
    }

    protected DataContext apply(DataContext context)
    {
        if (!this.customData.isEmpty())
        {
            context = context.copy();
            context.parse(this.customData);
        }

        return context;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("customData", this.customData);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.customData = data.getString("customData");
    }
}