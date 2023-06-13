package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.resources.Link;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Similar to {@link UIStringList}, but uses {@link Link}s
 */
public class UILinkList extends UIList<Link>
{
    public UILinkList(Consumer<List<Link>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list, (a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        return true;
    }
}