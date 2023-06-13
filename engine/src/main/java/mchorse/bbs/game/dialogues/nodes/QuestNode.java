package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.quests.Quest;

public class QuestNode extends EventBaseNode
{
    public String quest = "";
    public boolean skipIfCompleted;

    @Override
    public int execute(EventContext context)
    {
        if (context instanceof DialogueContext)
        {
            Entity player = context.data.getPlayer();

            if (this.skipIfCompleted && this.isPlayerCompletedQuest(player))
            {
                return EventBaseNode.ALL;
            }

            ((DialogueContext) context).addReply(this);
        }

        return EventBaseNode.HALT;
    }

    private boolean isPlayerCompletedQuest(Entity player)
    {
        Quest quest = BBSData.getQuests().load(this.quest);

        if (quest != null)
        {
            PlayerComponent character = player.get(PlayerComponent.class);

            if (character != null)
            {
                return character.states.wasQuestCompleted(this.quest);
            }
        }

        return false;
    }

    @Override
    protected String getDisplayTitle()
    {
        return this.quest;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("quest", this.quest);
        data.putBool("skipIfCompleted", this.skipIfCompleted);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("quest"))
        {
            this.quest = data.getString("quest");
        }

        this.skipIfCompleted = data.getBool("skipIfCompleted");
    }
}
