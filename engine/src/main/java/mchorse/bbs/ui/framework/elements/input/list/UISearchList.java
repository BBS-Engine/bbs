package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;

public class UISearchList <T> extends UIElement
{
    public UITextbox search;
    public UIList<T> list;

    public UISearchList(UIList<T> list)
    {
        this.search = new UITextbox(100, (str) -> this.filter(str, false));
        this.search.relative(this).set(0, 0, 0, 20).w(1, 0);

        this.list = list;
        this.list.relative(this).set(0, 20, 0, 0).w(1, 0).h(1, -20);

        this.add(this.search, this.list);
    }

    public UISearchList<T> label(IKey label)
    {
        this.search.textbox.setPlaceholder(label);

        return this;
    }

    public void filter(String str, boolean fill)
    {
        if (fill)
        {
            this.search.setText(str);
        }

        this.list.filter(str);
    }
}