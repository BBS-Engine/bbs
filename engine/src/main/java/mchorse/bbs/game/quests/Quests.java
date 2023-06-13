package mchorse.bbs.game.quests;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Quests implements IMapSerializable
{
    public Map<String, Quest> quests = new LinkedHashMap<String, Quest>();
    public boolean iterating;
    public List<Quest> toAdd = new ArrayList<Quest>(2);

    public void initiate(Entity player)
    {
        for (Quest quest : this.quests.values())
        {
            quest.initiate(player);
        }
    }

    public boolean add(Quest quest, Entity player)
    {
        if (this.has(quest.getId()))
        {
            return false;
        }

        if (this.iterating)
        {
            this.toAdd.add(quest);

            return true;
        }

        this.quests.put(quest.getId(), quest);
        quest.initiate(player);
        quest.accept.trigger(player);

        return true;
    }

    public boolean complete(String id, Entity player)
    {
        return this.remove(id, player, true);
    }

    public boolean decline(String id, Entity player)
    {
        return this.remove(id, player, false);
    }

    public boolean remove(String id, Entity player, boolean reward)
    {
        Quest quest = this.quests.remove(id);

        if (quest == null)
        {
            return false;
        }

        if (reward)
        {
            quest.reward(player);
        }
        else
        {
            quest.decline.trigger(player);
        }

        return true;
    }

    public boolean has(String id)
    {
        return this.quests.containsKey(id);
    }

    public Quest getByName(String id)
    {
        return this.quests.get(id);
    }

    public void copy(Quests quests)
    {
        this.quests.clear();

        for (Map.Entry<String, Quest> entry : quests.quests.entrySet())
        {
            Quest quest = BBSData.getQuests().load(entry.getKey());

            quest.partialFromData(entry.getValue().partialToData());
            this.quests.put(entry.getKey(), quest);
        }
    }

    public void flush(Entity player)
    {
        if (this.iterating)
        {
            this.iterating = false;

            for (Quest quest : this.toAdd)
            {
                this.add(quest, player);
            }

            this.toAdd.clear();
        }
    }

    @Override
    public void toData(MapType data)
    {
        for (Map.Entry<String, Quest> entry : this.quests.entrySet())
        {
            data.put(entry.getKey(), entry.getValue().partialToData());
        }
    }

    @Override
    public void fromData(MapType data)
    {
        for (String key : data.keys())
        {
            Quest quest = BBSData.getQuests().load(key);

            if (quest != null)
            {
                quest.partialFromData(data.getMap(key));
                this.quests.put(key, quest);
            }
        }
    }
}