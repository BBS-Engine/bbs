package mchorse.bbs.utils.files;

import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.utils.files.entries.AbstractEntry;
import mchorse.bbs.utils.files.entries.FolderEntry;

import java.util.Comparator;

/**
 * File tree
 * 
 * The implementations of file tree are responsible for creating full 
 * tree of files, so that {@link UITexturePicker} could navigate it.
 */
public abstract class FileTree
{
    /**
     * Abstract entry sorter. Sorts folders on top first, and then by
     * the display title name   
     */
    public static Comparator<AbstractEntry> SORTER = new EntrySorter();

    /**
     * Root entry, this top folder should be populated in
     */
    public FolderEntry root = new FolderEntry("root", null, null);

    /**
     * Adds a "back to parent directory" entry 
     */
    public static void addBackEntry(FolderEntry entry)
    {
        if (entry.parent == null)
        {
            return;
        }

        FolderEntry top = new FolderEntry("../", entry.parent != null ? entry.parent.file : null, entry);

        top.setTop(entry.parent);
        entry.getRawEntries().add(0, top);
    }

    /**
     * Get a top level folder for given name    
     */
    public FolderEntry getEntryForName(String name)
    {
        for (AbstractEntry entry : this.root.getEntries())
        {
            if (entry instanceof FolderEntry && entry.title.equalsIgnoreCase(name))
            {
                return (FolderEntry) entry;
            }
        }

        return this.root;
    }

    /**
     * Get a folder entry by path 
     */
    public FolderEntry getByPath(String path)
    {
        return this.getByPath(path, this.root);
    }

    /**
     * Get a folder entry by path with a default value, if given path 
     * wasn't found 
     */
    public FolderEntry getByPath(String path, FolderEntry orDefault)
    {
        FolderEntry entry = this.root;
        String[] segments = path.trim().split("/");

        for (String segment : segments)
        {
            for (AbstractEntry folder : entry.getEntries())
            {
                if (folder.isFolder() && folder.title.equalsIgnoreCase(segment))
                {
                    entry = (FolderEntry) folder;
                }
            }

        }

        return this.root == entry ? orDefault : entry;
    }
}