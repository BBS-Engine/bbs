package mchorse.bbs.utils.files.entries;

import mchorse.bbs.resources.Link;

import java.io.File;
import java.util.Objects;

public class FileEntry extends AbstractEntry
{
    public Link resource;

    public FileEntry(String title, File file, Link resource)
    {
        super(title, file);

        this.resource = resource;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof FileEntry)
        {
            result = result && Objects.equals(this.resource, ((FileEntry) obj).resource);
        }

        return result;
    }
}
