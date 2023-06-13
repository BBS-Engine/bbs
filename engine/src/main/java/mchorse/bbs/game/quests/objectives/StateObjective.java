package mchorse.bbs.game.quests.objectives;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.world.entities.Entity;

public class StateObjective extends Objective
{
    public Condition condition = new Condition();
    private boolean result;
    private String compiledMessage;

    @Override
    public void initiate(Entity player)
    {
        super.initiate(player);

        if (this.message.contains("${"))
        {
            this.compiledMessage = new DataContext(player).process(this.message);
        }
    }

    @Override
    public boolean isComplete(Entity player)
    {
        return this.result;
    }

    public boolean updateValue(Entity player)
    {
        boolean result = this.result;
        DataContext data = new DataContext(player);

        this.result = this.condition.execute(data);

        if (this.message.contains("${"))
        {
            this.compiledMessage = data.process(this.message);

            return true;
        }

        return this.result != result;
    }

    @Override
    public void complete(Entity player)
    {}

    @Override
    public String stringifyObjective(Entity player)
    {
        return this.compiledMessage == null ? this.message : this.compiledMessage;
    }

    @Override
    public void partialToData(MapType data)
    {
        if (this.result)
        {
            data.putBool("result", this.result);
        }
    }

    @Override
    public void partialFromData(MapType data)
    {
        if (data.has("result"))
        {
            this.result = data.getBool("result");
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("expression", this.condition.toData());
        data.putBool("result", this.result);

        if (this.compiledMessage != null)
        {
            data.putString("compiledMessage", this.compiledMessage);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.condition.fromData(data.getMap("expression"));
        this.result = data.getBool("result");

        if (data.has("compiledMessage"))
        {
            this.compiledMessage = data.getString("compiledMessage");
        }
        else
        {
            this.compiledMessage = null;
        }
    }
}