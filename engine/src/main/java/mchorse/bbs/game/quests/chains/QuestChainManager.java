package mchorse.bbs.game.quests.chains;

import mchorse.bbs.BBSData;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.manager.BaseManager;
import mchorse.bbs.world.entities.Entity;

import java.io.File;

public class QuestChainManager extends BaseManager<QuestChain>
{
    public QuestChainManager(File folder)
    {
        super(folder);
    }

    @Override
    protected QuestChain createData(String id, MapType data)
    {
        QuestChain chain = new QuestChain();

        if (data != null)
        {
            chain.fromData(data);
        }

        return chain;
    }

    public QuestInfo evaluate(String id, Entity player, String subject)
    {
        PlayerComponent character = player.get(PlayerComponent.class);
        QuestChain chain = this.load(id);

        if (character == null || chain == null)
        {
            return null;
        }

        DataContext data = new DataContext(player);

        for (QuestEntry entry : chain.entries)
        {
            String questId = entry.quest;

            if (questId.isEmpty() || !entry.condition.execute(data))
            {
                return null;
            }

            if (character.quests.has(questId) && subject.equals(entry.receiver))
            {
                Quest quest = character.quests.getByName(questId);

                return new QuestInfo(quest, quest.isComplete(player) ? QuestStatus.COMPLETED : QuestStatus.UNAVAILABLE);
            }

            boolean wasCompleted = character.states.wasQuestCompleted(questId);

            if (!wasCompleted && subject.equals(entry.provider))
            {
                Quest quest = BBSData.getQuests().load(questId);

                if (quest != null)
                {
                    return new QuestInfo(quest, QuestStatus.AVAILABLE);
                }
            }
        }

        return null;
    }
}