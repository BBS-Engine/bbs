package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.ui.framework.UIContext;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UIStringList extends UIList<String>
{
    public UIStringList(Consumer<List<String>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list);

        return true;
    }

    @Override
    protected String elementToString(UIContext context, int i, String element)
    {
        return element;
    }
}