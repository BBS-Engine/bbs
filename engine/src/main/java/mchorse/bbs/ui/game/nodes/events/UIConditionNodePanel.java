package mchorse.bbs.ui.game.nodes.events;

import mchorse.bbs.game.events.nodes.ConditionNode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.game.conditions.UICondition;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.utils.UI;

public class UIConditionNodePanel extends UIEventBaseNodePanel<ConditionNode>
{
    public UICondition checker;

    public UIConditionNodePanel()
    {
        super();

        this.checker = new UICondition();

        this.add(UI.label(UIKeys.NODES_EVENT_CONDITION).marginTop(12), this.checker, this.binary);
    }

    @Override
    public void set(ConditionNode node)
    {
        super.set(node);

        this.checker.set(node.condition);
    }
}