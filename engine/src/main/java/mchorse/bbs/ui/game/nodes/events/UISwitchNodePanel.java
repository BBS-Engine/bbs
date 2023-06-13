package mchorse.bbs.ui.game.nodes.events;

import mchorse.bbs.game.events.nodes.SwitchNode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.utils.UI;

public class UISwitchNodePanel extends UIEventBaseNodePanel<SwitchNode>
{
    public UITextEditor expression;

    public UISwitchNodePanel()
    {
        super();

        this.expression = new UITextEditor((t) -> this.node.expression = t);
        this.expression.background().h(80);

        this.add(UI.label(UIKeys.CONDITIONS_EXPRESSION).marginTop(12), this.expression);
    }

    @Override
    public void set(SwitchNode node)
    {
        super.set(node);

        this.expression.setText(node.expression);
    }
}