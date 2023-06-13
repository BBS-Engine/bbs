package mchorse.bbs.game.quests;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.dialogues.nodes.QuestChainNode;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.chains.QuestStatus;
import mchorse.bbs.game.utils.manager.BaseManager;
import mchorse.bbs.world.entities.Entity;

import java.io.File;

public class QuestManager extends BaseManager<Quest>
{
    public QuestManager(File folder)
    {
        super(folder);
    }

    @Override
    protected Quest createData(String id, MapType mapType)
    {
        Quest quest = new Quest();

        if (mapType != null)
        {
            quest.fromData(mapType);
        }

        return quest;
    }

    public void performQuestAction(IBridge bridge, String id, QuestStatus status)
    {
        Entity player = bridge.get(IBridgePlayer.class).getController();
        PlayerComponent character = player.get(PlayerComponent.class);

        if (status == QuestStatus.AVAILABLE)
        {
            Quest quest = this.load(id);

            if (quest != null)
            {
                character.quests.add(quest, player);
            }
        }
        else if (status == QuestStatus.COMPLETED)
        {
            Quest quest = character.quests.getByName(id);

            if (quest != null && quest.isComplete(player))
            {
                character.quests.complete(id, player);

                /* Update quests, because there might be some new quests down the chain */
                DialogueContext context = character.getDialogueContext();
                QuestChainNode questChain = context.getFirstReplyNodeAs(QuestChainNode.class);

                if (questChain != null)
                {
                    BBSData.getDialogues().handleContext(player, character.getDialogue(), context, null);
                }
            }
        }
        else if (status == QuestStatus.CANCELED)
        {
            Quest quest = character.quests.getByName(id);

            if (quest != null && quest.cancelable)
            {
                character.quests.remove(id, player, false);
            }
        }
    }
}