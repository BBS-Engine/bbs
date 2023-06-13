package mchorse.bbs.game.quests.chains;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;

public class QuestEntry implements IMapSerializable
{
    public String quest = "";
    public String provider = "";
    public String receiver = "";

    public Condition condition = new Condition(true);

    @Override
    public void toData(MapType data)
    {
        if (!this.quest.isEmpty())
        {
            data.putString("quest", this.quest);
        }

        if (!this.provider.isEmpty())
        {
            data.putString("provider", this.provider);
        }

        if (!this.receiver.isEmpty())
        {
            data.putString("receiver", this.receiver);
        }

        data.put("condition", this.condition.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("quest"))
        {
            this.quest = data.getString("quest");
        }

        if (data.has("provider"))
        {
            this.provider = data.getString("provider");
        }

        if (data.has("receiver"))
        {
            this.receiver = data.getString("receiver");
        }

        this.condition.fromData(data.getMap("condition"));
    }
}