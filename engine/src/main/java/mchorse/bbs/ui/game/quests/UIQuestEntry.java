package mchorse.bbs.ui.game.quests;

import mchorse.bbs.game.quests.chains.QuestEntry;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.conditions.UICondition;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;

public class UIQuestEntry extends UIElement
{
    public UIButton quest;
    public UITextbox giver;
    public UITextbox receiver;
    public UICondition condition;

    private QuestEntry entry;

    public UIQuestEntry()
    {
        super();

        this.quest = new UIButton(UIKeys.OVERLAYS_QUEST, (b) -> this.openQuests());
        this.giver = new UITextbox(10000, (text) -> this.entry.provider = text);
        this.receiver = new UITextbox(10000, (text) -> this.entry.receiver = text);
        this.condition = new UICondition();
        this.condition.tooltip(UIKeys.NODES_DIALOGUE_CONDITION, Direction.TOP);

        this.add(this.quest);
        this.add(UI.label(UIKeys.QUEST_CHAINS_PROVIDER).marginTop(12), this.giver);
        this.add(UI.label(UIKeys.QUEST_CHAINS_RECEIVER).marginTop(12), this.receiver);
        this.add(this.condition);

        this.column().vertical().stretch().padding(10);
    }

    private void openQuests()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.QUESTS, this.entry.quest, (name) -> this.entry.quest = name);
    }

    public void set(QuestEntry entry)
    {
        this.entry = entry;

        this.giver.setText(entry.provider);
        this.receiver.setText(entry.receiver);
        this.condition.set(entry.condition);
    }
}