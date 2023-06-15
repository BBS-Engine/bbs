package mchorse.bbs.ui.game.nodes;

import mchorse.bbs.game.utils.nodes.Node;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

public abstract class UINodePanel <T extends Node> extends UIElement
{
    public UITextbox title;

    public T node;

    public UINodePanel()
    {
        super();

        this.title = new UITextbox(1000, (t) -> this.node.title = t);

        this.column().vertical().stretch().padding(10);
        this.add(UI.label(UIKeys.NODES_NODE_TITLE), this.title);

        this.blockInsideEvents();
    }

    public void set(T node)
    {
        this.node = node;

        this.title.setText(node.title);
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.box(this.area.x, this.area.ey() - 40, this.area.ex(), this.area.ey(), Colors.A50);
        context.batcher.gradientVBox(this.area.x, this.area.y, this.area.ex(), this.area.ey() - 40, 0, Colors.A50);

        super.render(context);
    }
}