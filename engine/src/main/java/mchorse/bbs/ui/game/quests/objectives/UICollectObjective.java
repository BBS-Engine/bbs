package mchorse.bbs.ui.game.quests.objectives;

import mchorse.bbs.game.quests.objectives.CollectObjective;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;

public class UICollectObjective extends UIObjective<CollectObjective>
{
    public UISlot stack;
    public UIToggle ignoreData;

    public UICollectObjective(CollectObjective objective)
    {
        super(objective);

        this.stack = new UISlot(0, (stack) -> this.objective.stack = stack.copy());
        this.ignoreData = new UIToggle(UIKeys.QUESTS_OBJECTIVE_COLLECT_IGNORE_DATA, (b) -> this.objective.ignoreData = b.getValue());
        this.ignoreData.tooltip(UIKeys.QUESTS_OBJECTIVE_COLLECT_IGNORE_DATA_TOOLTIP);

        this.stack.setStack(objective.stack);
        this.stack.relative(this).x(1F, -129).y(1F).anchorY(1F);
        this.stack.tooltip(UIKeys.QUESTS_OBJECTIVE_COLLECT_TITLE);
        this.ignoreData.setValue(objective.ignoreData);
        this.ignoreData.relative(this).x(1F, -100).y(1F).wh(100, 24).anchorY(1F);

        this.message.relative(this).y(1F).w(1F, -134).h(24).anchorY(1F);

        this.h(36);

        this.add(this.stack, this.message, this.ignoreData);
    }

    @Override
    public IKey getMessageTooltip()
    {
        return UIKeys.QUESTS_OBJECTIVE_COLLECT_MESSAGE_TOOLTIP;
    }
}