package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.utils.resources.FilteredLink;

import java.util.List;
import java.util.function.Consumer;

public class UIFilteredLinkList extends UIList<FilteredLink>
{
    public UIFilteredLinkList(Consumer<List<FilteredLink>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        return true;
    }
}