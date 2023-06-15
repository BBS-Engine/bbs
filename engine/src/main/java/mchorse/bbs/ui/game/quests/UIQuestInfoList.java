package mchorse.bbs.ui.game.quests;

import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class UIQuestInfoList extends UIList<QuestInfo>
{
    public UIQuestInfoList(Consumer<List<QuestInfo>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing(a -> a.quest.title));

        return true;
    }

    @Override
    protected void renderElementPart(UIContext context, QuestInfo element, int i, int x, int y, boolean hover, boolean selected)
    {
        context.batcher.textShadow(this.elementToString(i, element), x + 4, y + this.scroll.scrollItemSize / 2 - context.font.getHeight() / 2, element.status.color.getRGBColor());
    }

    @Override
    protected String elementToString(int i, QuestInfo element)
    {
        return element.quest.getProcessedTitle();
    }
}