package mchorse.bbs.ui.font;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class UIGlyphList extends UIList<Integer>
{
    public UIGlyphList(Consumer<List<Integer>> callback)
    {
        super(callback);
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparingInt((a) -> a));

        return true;
    }

    @Override
    protected String elementToString(UIContext context, int i, Integer element)
    {
        return element + " - " + Character.getName(element).toLowerCase();
    }
}