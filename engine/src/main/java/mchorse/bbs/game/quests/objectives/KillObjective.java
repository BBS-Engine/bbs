package mchorse.bbs.game.quests.objectives;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;

public class KillObjective extends Objective
{
    public String entity = "";
    public MapType data;
    public int count;
    public int killed;

    public KillObjective()
    {}

    public void playerKilled(Entity player, Entity mob)
    {
        if (this.entity.equals(mob.id))
        {
            if (!this.compareTag(mob))
            {
                return;
            }

            this.killed += 1;
        }
    }

    private boolean compareTag(Entity entity)
    {
        if (this.data == null)
        {
            return true;
        }

        return this.compareTagPartial(this.data, entity.toData());
    }

    private boolean compareTagPartial(BaseType a, BaseType b)
    {
        if (BaseType.isMap(a) && BaseType.isMap(b))
        {
            MapType dataA = (MapType) a;
            MapType dataB = (MapType) b;

            for (String key : dataA.keys())
            {
                BaseType baseType = dataB.get(key);

                if (!this.compareTagPartial(dataA.get(key), baseType))
                {
                    return false;
                }
            }

            return true;
        }

        return a.equals(b);
    }

    @Override
    public boolean isComplete(Entity player)
    {
        return this.killed >= this.count;
    }

    @Override
    public void complete(Entity player)
    {}

    @Override
    public String stringifyObjective(Entity player)
    {
        String entity = player.id.toString();
        int count = Math.min(this.killed, this.count);

        if (entity != null)
        {
            entity = UIKeys.C_ENTITIES.get(entity).get();
        }
        else
        {
            entity = this.entity;
        }

        if (!this.message.isEmpty())
        {
            return this.message.replace("${entity}", entity)
                .replace("${count}", String.valueOf(count))
                .replace("${total}", String.valueOf(this.count));
        }

        return UIKeys.QUESTS_OBJECTIVE_KILL_STRING.formatString(entity, count, this.count);
    }

    @Override
    public void partialToData(MapType data)
    {
        data.putInt("killed", this.killed);
    }

    @Override
    public void partialFromData(MapType data)
    {
        if (data.has("killed"))
        {
            this.killed = data.getInt("killed");
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("entity", this.entity.toString());

        if (this.data != null)
        {
            data.put("data", this.data);
        }

        data.putInt("count", this.count);
        data.putInt("killed", this.killed);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.entity = data.getString("entity");

        if (data.has("data"))
        {
            this.data = data.getMap("data");
        }

        this.count = data.getInt("count");
        this.killed = data.getInt("killed");
    }
}