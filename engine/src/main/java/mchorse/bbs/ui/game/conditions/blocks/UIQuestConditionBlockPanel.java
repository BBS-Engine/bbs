package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.QuestConditionBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;

public class UIQuestConditionBlockPanel extends UIConditionBlockPanel<QuestConditionBlock>
{
    public UIButton id;
    public UITarget target;
    public UICirculate quest;

    public UIQuestConditionBlockPanel(UIConditionOverlayPanel overlay, QuestConditionBlock block)
    {
        super(overlay, block);

        this.id = new UIButton(UIKeys.OVERLAYS_QUEST, (t) -> this.openQuests());
        this.target = new UITarget(block.target).skip(TargetMode.NPC);
        this.quest = new UICirculate(this::toggleQuest);

        for (QuestConditionBlock.QuestCheck check : QuestConditionBlock.QuestCheck.values())
        {
            this.quest.addLabel(UIKeys.C_QUEST_CHECK.get(check));
        }

        this.quest.setValue(block.quest.ordinal());

        this.add(UI.row(
            UI.column(UI.label(UIKeys.CONDITIONS_QUEST_ID).marginTop(12), this.id),
            UI.column(UI.label(UIKeys.CONDITIONS_QUEST_CHECK).marginTop(12), this.quest)
        ));
        this.add(this.target.marginTop(12));
    }

    private void openQuests()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.QUESTS, this.block.id, (name) -> this.block.id = name);
    }

    private void toggleQuest(UICirculate b)
    {
        this.block.quest = QuestConditionBlock.QuestCheck.values()[b.getValue()];
    }
}