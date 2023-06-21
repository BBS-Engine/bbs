package mchorse.bbs.ui.game.nodes.events;

import mchorse.bbs.game.events.nodes.TriggerNode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;

public class UITriggerNodePanel extends UIEventBaseNodePanel<TriggerNode>
{
    public UITrigger trigger;
    public UITextbox customData;
    public UIToggle cancel;

    public UITriggerNodePanel()
    {
        super();

        this.trigger = new UITrigger();
        this.customData = new UITextbox(100000, (text) -> this.node.customData = text);
        this.customData.tooltip(UIKeys.NODES_EVENT_DATA_TOOLTIP);
        this.cancel = new UIToggle(UIKeys.NODES_EVENT_CANCEL, (b) -> this.node.cancel = b.getValue());
        this.cancel.tooltip(UIKeys.NODES_EVENT_CANCEL_TOOLTIP, Direction.TOP);

        this.add(this.trigger);
        this.add(UI.label(UIKeys.NODES_EVENT_DATA).marginTop(12), this.customData, this.cancel, this.binary);
    }

    @Override
    public void set(TriggerNode node)
    {
        super.set(node);

        this.trigger.set(node.trigger);
        this.customData.setText(node.customData);
        this.cancel.setValue(node.cancel);
    }
}