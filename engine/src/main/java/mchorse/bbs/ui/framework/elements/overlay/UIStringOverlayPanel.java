package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UIStringOverlayPanel extends UIOverlayPanel
{
    public UISearchList<String> strings;

    private Consumer<String> callback;
    private boolean none;

    public static UIStringOverlayPanel links(IKey title, Collection<Link> links, Consumer<Link> callback)
    {
        return links(title, true, links, callback);
    }

    public static UIStringOverlayPanel links(IKey title, boolean none, Collection<Link> links, Consumer<Link> callback)
    {
        Collection<String> strings = links.stream().map((a) -> a.toString()).collect(Collectors.toList());
        UIStringOverlayPanel panel = new UIStringOverlayPanel(title, none, strings, (str) ->
        {
            if (callback != null)
            {
                callback.accept(str.isEmpty() ? null : Link.create(str));
            }
        });

        return panel;
    }

    public UIStringOverlayPanel(IKey title, Collection<String> strings, Consumer<String> callback)
    {
        this(title, true, strings, callback);
    }

    public UIStringOverlayPanel(IKey title, boolean none, Collection<String> strings, Consumer<String> callback)
    {
        super(title);

        this.none = none;
        this.callback = callback;

        this.strings = new UISearchList<>(new UIStringList((list) -> this.accept(list.get(0))));
        this.strings.label(UIKeys.SEARCH);
        this.strings.relative(this.content).full().x(6).w(1F, -12);

        this.strings.list.add(strings);
        this.strings.list.sort();
        this.strings.list.scroll.scrollSpeed *= 2;

        if (this.none)
        {
            this.strings.list.getList().add(0, UIKeys.NONE.get());
            this.strings.list.update();
        }

        this.content.add(this.strings);
    }

    public UIStringOverlayPanel set(String string)
    {
        this.strings.filter("", true);
        this.strings.list.setCurrentScroll(string);

        if (this.none && this.strings.list.isDeselected())
        {
            this.strings.list.setIndex(0);
        }

        return this;
    }

    public UIStringOverlayPanel set(Link link)
    {
        return this.set(link == null ? "" : link.toString());
    }

    public UIStringOverlayPanel callback(Consumer<String> callback)
    {
        this.callback = callback;

        return this;
    }

    protected void accept(String string)
    {
        if (this.callback != null)
        {
            this.callback.accept(this.getValue(string));
        }
    }

    protected String getValue()
    {
        return this.getValue(this.strings.list.getCurrentFirst());
    }

    protected String getValue(String string)
    {
        if (!this.none)
        {
            return string;
        }

        return this.strings.list.getIndex() == 0 ? "" : string;
    }
}
