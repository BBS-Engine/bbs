package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;

public class QuestConditionBlock extends TargetConditionBlock
{
    public QuestCheck quest = QuestCheck.COMPLETED;

    @Override
    public boolean evaluateBlock(DataContext context)
    {
        PlayerComponent character = this.target.getCharacter(context);

        if (character != null)
        {
            if (this.quest == QuestCheck.ABSENT)
            {
                return !character.states.wasQuestCompleted(this.id) && !character.quests.has(this.id);
            }
            else if (this.quest == QuestCheck.PRESENT)
            {
                return character.quests.has(this.id);
            }
            else
            {
                return character.states.wasQuestCompleted(this.id);
            }
        }

        return false;
    }

    @Override
    protected TargetMode getDefaultTarget()
    {
        return TargetMode.SUBJECT;
    }

    @Override
    public String stringify()
    {
        if (this.quest == QuestCheck.ABSENT)
        {
            return UIKeys.CONDITIONS_QUEST_IS_ABSENT.formatString(this.id);
        }
        else if (this.quest == QuestCheck.PRESENT)
        {
            return UIKeys.CONDITIONS_QUEST_IS_PRESENT.formatString(this.id);
        }

        return UIKeys.CONDITIONS_QUEST_IS_COMPLETED.formatString(this.id);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("quest", this.quest.ordinal());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.quest = EnumUtils.getValue(data.getInt("quest"), QuestCheck.values(), QuestCheck.COMPLETED);
    }

    public static enum QuestCheck
    {
        ABSENT, PRESENT, COMPLETED
    }
}