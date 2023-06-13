package mchorse.bbs.ui.game.quests;

import mchorse.bbs.game.quests.chains.QuestEntry;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.List;
import java.util.function.Consumer;

public class UIQuestEntryList extends UIList<QuestEntry>
{
    public UIQuestEntryList(Consumer<List<QuestEntry>> callback)
    {
        super(callback);
    }

    @Override
    protected String elementToString(int i, QuestEntry element)
    {
        return element.quest.trim().isEmpty() ? "-" : element.quest;
    }
}