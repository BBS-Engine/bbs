package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.l10n.keys.IKey;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UILabelList <T> extends UIList<Label<T>>
{
    public UILabelList(Consumer<List<Label<T>>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    public void add(IKey title, T value)
    {
        this.add(new Label<T>(title, value));
    }

    public void setCurrentTitle(String title)
    {
        for (int i = 0; i < this.list.size(); i ++)
        {
            if (this.list.get(i).title.equals(title))
            {
                this.setIndex(i);

                return;
            }
        }
    }

    public void setCurrentValue(T value)
    {
        for (int i = 0; i < this.list.size(); i ++)
        {
            if (this.list.get(i).value.equals(value))
            {
                this.setIndex(i);

                return;
            }
        }
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list, (a, b) -> a.title.get().compareToIgnoreCase(b.title.get()));

        return true;
    }

    @Override
    protected String elementToString(int i, Label<T> element)
    {
        return element.title.get();
    }
}