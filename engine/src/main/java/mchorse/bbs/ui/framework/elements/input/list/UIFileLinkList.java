package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.BBS;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UIFileLinkList extends UIList<UIFileLinkList.FileLink>
{
    public Consumer<Link> fileCallback;
    public Link path = new Link("", "");
    public Predicate<Link> filter;

    public UIFileLinkList(Consumer<Link> fileCallback)
    {
        super(null);

        this.callback = (list) ->
        {
            FileLink fileLink = list.get(0);

            if (!fileLink.folder)
            {
                if (this.fileCallback != null)
                {
                    this.fileCallback.accept(fileLink.link);
                }
            }
            else
            {
                this.setPath(fileLink.link);
            }
        };
        this.fileCallback = fileCallback;
        this.scroll.scrollItemSize = 16;
        this.scroll.scrollSpeed = 16;
    }

    public UIFileLinkList filter(Predicate<Link> filter)
    {
        this.filter = filter;

        return this;
    }

    /**
     * Set current link
     */
    public void setPath(Link link)
    {
        if (link == null || link.source.isEmpty())
        {
            this.clear();

            for (String source : BBS.getProvider().getSourceKeys())
            {
                this.add(new FileLink(source, new Link(source, ""), true));
            }

            this.path = new Link("", "");

            this.sort();
        }
        else
        {
            Collection<Link> links = BBS.getProvider().getLinksFromPath(link, false);

            if (links.size() == 1)
            {
                Link first = links.iterator().next();

                if (first.path.endsWith("/"))
                {
                    this.setPath(first);

                    return;
                }
            }

            this.path = link;

            FileLink parent = link.path.isEmpty()
                ? new FileLink("..", new Link("", ""), true)
                : new FileLink("..", new Link(link.source, StringUtils.parentPath(link.path)), true);

            this.clear();
            this.add(parent);

            for (Link l : links)
            {
                if (this.filter == null || this.filter.test(l))
                {
                    this.add(new FileLink(StringUtils.fileName(l.path).replaceAll("/", ""), l, l.path.endsWith("/")));
                }
            }

            this.sort();
        }
    }

    public void setCurrent(Link link)
    {
        this.deselect();

        if (link == null)
        {
            return;
        }

        for (int i = 0, c = this.list.size(); i < c; i++)
        {
            FileLink entry = this.list.get(i);

            if (entry.link.equals(link))
            {
                this.setIndex(i);

                return;
            }
        }
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort((a, b) ->
        {
            if (a.folder != b.folder)
            {
                return a.folder ? -1 : 1;
            }

            return a.title.compareTo(b.title);
        });

        return true;
    }

    @Override
    protected void renderElementPart(UIContext context, FileLink element, int i, int x, int y, boolean hover, boolean selected)
    {
        context.batcher.icon(element.folder ? Icons.FOLDER : Icons.IMAGE, Colors.setA(Colors.WHITE, hover ? 0.75F : 0.6F), x + 2, y);
        context.batcher.textShadow(context.font, element.title, x + 20, y + 4, hover ? Colors.HIGHLIGHT : Colors.WHITE);
    }

    public static class FileLink
    {
        public String title;
        public Link link;
        public boolean folder;

        public FileLink(String title, Link link, boolean folder)
        {
            this.title = title;
            this.link = link;
            this.folder = folder;
        }
    }
}