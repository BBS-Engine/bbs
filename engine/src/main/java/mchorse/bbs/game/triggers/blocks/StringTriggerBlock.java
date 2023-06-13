package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.MapType;

public abstract class StringTriggerBlock extends TriggerBlock
{
    public String id = "";

    public StringTriggerBlock()
    {}

    @Override
    public String stringify()
    {
        if (this.id.isEmpty())
        {
            return super.stringify();
        }

        return this.id;
    }

    @Override
    public boolean isEmpty()
    {
        return this.id.isEmpty();
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString(this.getKey(), this.id);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.id = data.getString(this.getKey());
    }

    protected abstract String getKey();
}