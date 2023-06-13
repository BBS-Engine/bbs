package mchorse.bbs.ui.game.nodes.dialogues;

import mchorse.bbs.game.dialogues.nodes.QuestNode;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;

public class UIQuestDialogueNodePanel extends UIEventBaseNodePanel<QuestNode>
{
    public UIButton quest;
    public UIToggle skipIfCompleted;

    public UIQuestDialogueNodePanel()
    {
        super();

        this.quest = new UIButton(UIKeys.OVERLAYS_QUEST, (b) -> this.openQuests());
        this.skipIfCompleted = new UIToggle(UIKeys.NODES_DIALOGUE_SKIP_IF_COMPLETED, (b) -> this.node.skipIfCompleted = b.getValue());
        this.skipIfCompleted.tooltip(UIKeys.NODES_DIALOGUE_SKIP_IF_COMPLETED_TOOLTIP);

        this.add(this.quest, this.skipIfCompleted.marginTop(12));
    }

    private void openQuests()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.QUESTS, this.node.quest, (name) -> this.node.quest = name);
    }
}