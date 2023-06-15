package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.files.entries.AbstractEntry;
import mchorse.bbs.utils.files.entries.FileEntry;
import mchorse.bbs.utils.files.entries.FolderEntry;

import java.util.List;
import java.util.function.Consumer;

/**
 * Folder entry list GUI
 * 
 * This GUI list element allows to navigate through the file tree 
 * entries. 
 */
public class UIFolderEntryList extends UIList<AbstractEntry>
{
    public Consumer<FileEntry> fileCallback;
    public Link link;
    public FolderEntry parent;

    public UIFolderEntryList(Consumer<FileEntry> fileCallback)
    {
        super(null);

        this.callback = (list) ->
        {
            AbstractEntry entry = list.get(0);

            if (entry instanceof FileEntry)
            {
                if (this.fileCallback != null)
                {
                    this.fileCallback.accept(((FileEntry) entry));
                }
            }
            else if (entry.isFolder())
            {
                this.setFolder((FolderEntry) entry);
            }
        };
        this.fileCallback = fileCallback;
        this.scroll.scrollItemSize = 16;
        this.scroll.scrollSpeed = 16;
    }

    /**
     * Set current folder
     */
    public void setFolder(FolderEntry folder)
    {
        /* Quick jump to children folder that has only one folder */
        if (folder.getEntries().size() <= 2 && !folder.isTop())
        {
            for (AbstractEntry subEntry : folder.getEntries())
            {
                if (subEntry.isFolder())
                {
                    FolderEntry subFolder = (FolderEntry) subEntry;

                    if (!subFolder.isTop())
                    {
                        this.setFolder(subFolder);

                        return;
                    }
                }
            }
        }

        this.setDirectFolder(folder);
    }

    public void setDirectFolder(FolderEntry folder)
    {
        List<AbstractEntry> entries = folder.getEntries();
        List<AbstractEntry> current = this.getCurrent();

        this.parent = folder;
        this.setList(entries);
        this.setCurrent(current.isEmpty() ? null : current.get(0));

        if (this.current.isEmpty())
        {
            this.setCurrent(this.link);
        }
    }

    public Link getCurrentLink()
    {
        List<AbstractEntry> entry = this.getCurrent();

        if (!entry.isEmpty() && entry.get(0) instanceof FileEntry)
        {
            return ((FileEntry) entry.get(0)).resource;
        }

        return null;
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
            AbstractEntry entry = this.list.get(i);

            if (entry instanceof FileEntry && ((FileEntry) entry).resource.equals(link))
            {
                this.setIndex(i);
                break;
            }
        }
    }

    @Override
    protected void renderElementPart(UIContext context, AbstractEntry element, int i, int x, int y, boolean hover, boolean selected)
    {
        context.batcher.icon(element instanceof FolderEntry ? Icons.FOLDER : Icons.SERVER, x + 2, y, Colors.setA(Colors.WHITE, hover ? 0.75F : 0.6F));
        context.batcher.textShadow(context.font, element.title, x + 20, y + 4, hover ? Colors.HIGHLIGHT : Colors.WHITE);
    }
}