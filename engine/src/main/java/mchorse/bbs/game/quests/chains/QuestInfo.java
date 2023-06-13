package mchorse.bbs.game.quests.chains;

import mchorse.bbs.game.quests.Quest;

public class QuestInfo
{
    public Quest quest;
    public QuestStatus status;

    public QuestInfo()
    {}

    public QuestInfo(Quest quest, QuestStatus status)
    {
        this.quest = quest;
        this.status = status;
    }
}