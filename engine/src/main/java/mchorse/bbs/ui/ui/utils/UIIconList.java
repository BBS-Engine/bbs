package mchorse.bbs.ui.ui.utils;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class UIIconList extends UIList<Icon>
{
    public UIIconList(Consumer<List<Icon>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 20;

        this.add(Icons.NONE);
        this.add(Icons.ICONS.values());
        this.sort();
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing(a -> a.id));

        return true;
    }

    @Override
    protected void renderElementPart(UIContext context, Icon element, int i, int x, int y, boolean hover, boolean selected)
    {
        context.batcher.icon(element, x + 10, y + 10, 0.5F, 0.5F);

        super.renderElementPart(context, element, i, x + 20, y, hover, selected);
    }

    @Override
    protected String elementToString(int i, Icon element)
    {
        return element.id;
    }
}