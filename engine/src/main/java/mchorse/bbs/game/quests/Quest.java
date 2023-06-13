package mchorse.bbs.game.quests;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.objectives.KillObjective;
import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.game.quests.objectives.StateObjective;
import mchorse.bbs.game.quests.rewards.Reward;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.manager.data.AbstractData;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Quest extends AbstractData implements IMapPartialSerializable
{
    public String title = "";
    public String story = "";
    public boolean cancelable = true;
    public boolean instant;

    public Trigger accept = new Trigger();
    public Trigger decline = new Trigger();
    public Trigger complete = new Trigger();

    public final List<Objective> objectives = new ArrayList<Objective>();
    public final List<Reward> rewards = new ArrayList<Reward>();

    private boolean initated;

    public void initiate(Entity player)
    {
        if (this.initated)
        {
            return;
        }

        for (Objective objective : this.objectives)
        {
            objective.initiate(player);
        }

        this.initated = true;
    }

    public String getProcessedTitle()
    {
        return TextUtils.processColoredText(this.title);
    }

    /* Quest building */

    public Quest setStory(String title, String story)
    {
        this.title = title;
        this.story = story;

        return this;
    }

    public Quest addObjective(Objective objective)
    {
        this.objectives.add(objective);

        return this;
    }

    public Quest addReward(Reward reward)
    {
        this.rewards.add(reward);

        return this;
    }

    /* Quest hooks */

    public void mobWasKilled(Entity player, Entity entity)
    {
        for (Objective objective : this.objectives)
        {
            if (objective instanceof KillObjective)
            {
                ((KillObjective) objective).playerKilled(player, entity);
            }
        }
    }

    public boolean stateWasUpdated(Entity player)
    {
        int i = 0;

        for (Objective objective : this.objectives)
        {
            if (objective instanceof StateObjective)
            {
                i += ((StateObjective) objective).updateValue(player) ? 1 : 0;
            }
        }

        return i > 0;
    }

    /* Rewards */

    public boolean isComplete(Entity player)
    {
        boolean result = true;

        for (Objective objective : this.objectives)
        {
            result = result && objective.isComplete(player);
        }

        return result;
    }

    public void reward(Entity player)
    {
        for (Objective objective : this.objectives)
        {
            objective.complete(player);
        }

        for (Reward reward : this.rewards)
        {
            reward.reward(player);
        }

        this.complete.trigger(player);

        /* Write down that the quest was completed */
        PlayerComponent character = player.get(PlayerComponent.class);

        if (character != null)
        {
            character.states.completeQuest(this.getId());
        }

        BBSData.getStates().completeQuest(this.getId());
    }

    public boolean rewardIfComplete(Entity player)
    {
        if (!this.isComplete(player))
        {
            return false;
        }

        this.reward(player);

        return true;
    }

    /* Serialization / deserialization */

    @Override
    public void partialToData(MapType data)
    {
        ListType objectives = new ListType();

        data.put("objectives", objectives);

        for (Objective objective : this.objectives)
        {
            objectives.add(objective.toData());
        }
    }

    @Override
    public void partialFromData(MapType data)
    {
        if (data.has("objectives"))
        {
            ListType list = data.getList("objectives");

            for (int i = 0; i < Math.min(list.size(), this.objectives.size()); i++)
            {
                this.objectives.get(i).partialFromData(list.getMap(i));
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType objectives = new ListType();
        ListType rewards = new ListType();

        data.putString("title", this.title);
        data.putString("story", this.story);
        data.putBool("cancelable", this.cancelable);
        data.putBool("instant", this.instant);

        MapType accept = this.accept.toData();
        MapType decline = this.decline.toData();
        MapType complete = this.complete.toData();

        if (!accept.isEmpty()) data.put("accept", accept);
        if (!decline.isEmpty()) data.put("decline", decline);
        if (!complete.isEmpty()) data.put("complete", complete);

        data.put("objectives", objectives);
        data.put("rewards", rewards);

        for (Objective objective : this.objectives)
        {
            objectives.add(BBS.getFactoryObjectives().toData(objective));
        }

        for (Reward reward : this.rewards)
        {
            rewards.add(BBS.getFactoryRewards().toData(reward));
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.title = data.getString("title");
        this.story = data.getString("story");

        if (data.has("cancelable"))
        {
            this.cancelable = data.getBool("cancelable");
        }

        if (data.has("instant"))
        {
            this.instant = data.getBool("instant");
        }

        if (data.has("accept"))
        {
            this.accept.fromData(data.getMap("accept"));
        }

        if (data.has("decline"))
        {
            this.decline.fromData(data.getMap("decline"));
        }

        if (data.has("complete"))
        {
            this.complete.fromData(data.getMap("complete"));
        }

        if (data.has("objectives"))
        {
            ListType list = data.getList("objectives");

            for (int i = 0; i < list.size(); i ++)
            {
                Objective objective = BBS.getFactoryObjectives().fromData(list.getMap(i));

                if (objective != null)
                {
                    this.objectives.add(objective);
                }
            }
        }

        if (data.has("rewards"))
        {
            ListType list = data.getList("rewards");

            for (int i = 0; i < list.size(); i ++)
            {
                Reward reward = BBS.getFactoryRewards().fromData(list.getMap(i));

                if (reward != null)
                {
                    this.rewards.add(reward);
                }
            }
        }
    }
}