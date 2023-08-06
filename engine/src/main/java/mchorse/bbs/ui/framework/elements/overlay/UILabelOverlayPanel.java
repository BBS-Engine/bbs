package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.utils.Label;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class UILabelOverlayPanel <T> extends UIOverlayPanel
{
    public UISearchList<Label<T>> labels;

    private Consumer<Label<T>> callback;

    public UILabelOverlayPanel(IKey title, Collection<Label<T>> keys, Consumer<Label<T>> callback)
    {
        super(title);

        this.callback = callback;

        this.labels = new UISearchList<>(new UILabelList<>((list) -> this.accept(list.get(0))));
        this.labels.label(UIKeys.SEARCH);
        this.labels.relative(this.content).full().x(6).w(1F, -12);

        for (Label<T> location : keys)
        {
            this.labels.list.add(location);
        }

        this.labels.list.sort();

        this.labels.list.update();
        this.labels.list.scroll.scrollSpeed *= 3;

        this.content.add(this.labels);
    }

    public UILabelOverlayPanel<T> set(T value)
    {
        this.labels.filter("", true);

        for (Label<T> label : this.labels.list.getList())
        {
            if (label.value.equals(value))
            {
                this.labels.list.setCurrentScroll(label);

                break;
            }
        }

        return this;
    }

    private void accept(Label<T> label)
    {
        if (this.callback != null)
        {
            this.callback.accept(label);
        }
    }
}
