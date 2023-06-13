package mchorse.bbs.ui.game.nodes;

import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.utils.Direction;

public abstract class UIEventBaseNodePanel <T extends EventBaseNode> extends UINodePanel<T>
{
    public UIToggle binary;

    public UIEventBaseNodePanel()
    {
        super();

        this.binary = new UIToggle(UIKeys.NODES_EVENT_BINARY, (b) -> this.node.binary = b.getValue());
        this.binary.tooltip(UIKeys.NODES_EVENT_BINARY_TOOLTIP, Direction.TOP);
    }

    public void set(T node)
    {
        super.set(node);

        this.binary.setValue(node.binary);
    }
}