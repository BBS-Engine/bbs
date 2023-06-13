package mchorse.bbs.utils.files.entries;

import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.files.FileTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FolderEntry extends AbstractEntry
{
    public FolderEntry parent;
    public FolderEntry top;
    protected List<AbstractEntry> entries = new ArrayList<AbstractEntry>();
    private long lastModified;

    public FolderEntry(String title, File file, FolderEntry parent)
    {
        super(title, file);

        this.parent = parent;
    }

    protected String getPrefix()
    {
        List<String> joiner = new ArrayList<String>();
        FolderEntry parent = this;

        while (parent != null && parent.parent != null)
        {
            joiner.add(parent.title);
            parent = parent.parent;
        }

        Collections.reverse(joiner);

        return String.join("/", joiner).replaceFirst("/", Link.SOURCE_SEPARATOR);
    }

    public List<AbstractEntry> getEntries()
    {
        if (this.top != null)
        {
            return this.top.getEntries();
        }
        else if (this.file != null)
        {
            if (this.hasChanged())
            {
                this.populateEntries();
            }

            this.lastModified = System.currentTimeMillis();
        }

        return this.entries;
    }

    public List<AbstractEntry> getRawEntries()
    {
        return this.entries;
    }

    protected void populateEntries()
    {
        Collections.sort(this.entries, FileTree.SORTER);

        if (this.getEntry("../") == null)
        {
            FileTree.addBackEntry(this);
        }
    }

    protected AbstractEntry getEntry(String title)
    {
        for (AbstractEntry entry : this.entries)
        {
            if (entry.title.equals(title)) return entry;
        }

        return null;
    }

    public void setTop(FolderEntry top)
    {
        this.top = top;
    }

    public boolean hasChanged()
    {
        if (this.top != null)
        {
            return false;
        }

        if (this.file != null && this.file.lastModified() > this.lastModified)
        {
            return true;
        }

        for (AbstractEntry entry : this.entries)
        {
            if (entry.isFolder())
            {
                FolderEntry folder = (FolderEntry) entry;

                if (folder.hasChanged())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isEmpty()
    {
        if (this.entries.size() == 1)
        {
            AbstractEntry entry = this.entries.get(0);

            if (entry.isFolder() && ((FolderEntry) entry).isTop())
            {
                return true;
            }
        }

        return this.entries.isEmpty();
    }

    public boolean isTop()
    {
        return this.top != null;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof FolderEntry)
        {
            result = result && Objects.equals(this.parent, ((FolderEntry) obj).parent);
        }

        return result;
    }
}
