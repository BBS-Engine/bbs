package mchorse.bbs.ui.framework.elements.context;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.context.ContextAction;

import java.util.List;
import java.util.function.Consumer;

public class UIActionList extends UIList<ContextAction>
{
    public UIActionList(Consumer<List<ContextAction>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 20;
    }

    @Override
    public void renderListElement(UIContext context, ContextAction element, int i, int x, int y, boolean hover, boolean selected)
    {
        int h = this.scroll.scrollItemSize;

        element.render(context, context.font, x, y, this.area.w, h, hover, selected);
    }
}