package mchorse.bbs.utils.files.entries;

import java.io.File;
import java.util.Objects;

/**
 * Abstract file tree entry class
 * 
 * This basic type is basically contained within file tree
 */
public abstract class AbstractEntry
{
    /**
     * Displayable title 
     */
    public String title;

    /**
     * Associated file
     */
    public File file;

    public AbstractEntry(String title, File file)
    {
        this.title = title;
        this.file = file;
    }

    public boolean isFolder()
    {
        return this instanceof FolderEntry;
    }

    public boolean exists()
    {
        return this.file == null ? true : this.file.exists();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractEntry)
        {
            AbstractEntry entry = (AbstractEntry) obj;

            return Objects.equals(this.title, entry.title) && Objects.equals(this.file, entry.file);
        }

        return super.equals(obj);
    }
}