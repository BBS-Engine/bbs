package mchorse.bbs.game.quests.objectives;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.quests.IMapPartialSerializable;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.world.entities.Entity;

public abstract class Objective implements IMapSerializable, IMapPartialSerializable
{
    public String message = "";

    public void initiate(Entity player)
    {}

    public abstract boolean isComplete(Entity player);

    public abstract void complete(Entity player);

    public String stringify(Entity player)
    {
        return TextUtils.processColoredText(this.stringifyObjective(player));
    }

    protected abstract String stringifyObjective(Entity player);

    @Override
    public void toData(MapType data)
    {
        data.putString("message", this.message);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("message"))
        {
            this.message = data.getString("message");
        }
    }
}