package mchorse.bbs.resources.packs;

import mchorse.bbs.resources.ISourcePack;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ExternalAssetsSourcePack implements ISourcePack
{
    public final String source;
    public final File folder;

    private boolean providesFiles;

    public static void getLinksFromPathRecursively(File folder, Collection<Link> links, Link link, String prefix, int i)
    {
        i -= 1;

        File[] files = folder.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            String path = StringUtils.combinePaths(prefix, file.getName());

            if (file.isDirectory() && i > 0)
            {
                getLinksFromPathRecursively(file, links, link, path, i);
            }

            links.add(new Link(link.source, path + (file.isDirectory() ? "/" : "")));
        }
    }

    public ExternalAssetsSourcePack(String source, File folder)
    {
        this.source = source;
        this.folder = folder;
    }

    public ExternalAssetsSourcePack providesFiles()
    {
        this.providesFiles = true;

        return this;
    }

    @Override
    public String getPrefix()
    {
        return this.source;
    }

    @Override
    public boolean hasAsset(Link link)
    {
        return this.getFileInternal(link).exists();
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        return new FileInputStream(this.getFileInternal(link));
    }

    @Override
    public File getFile(Link link)
    {
        return this.providesFiles ? this.getFileInternal(link) : null;
    }

    @Override
    public Link getLink(File file)
    {
        String fullPath = this.folder.getAbsolutePath();
        String filePath = file.getAbsolutePath();

        if (filePath.startsWith(fullPath))
        {
            String path = filePath.substring(fullPath.length());

            if (path.charAt(0) == '/' || path.charAt(0) == '\\')
            {
                path = path.substring(1);
            }

            return new Link(this.source, path.replaceAll("\\\\", "/"));
        }

        return null;
    }

    private File getFileInternal(Link link)
    {
        return new File(this.folder, link.path);
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
        File folder = this.getFileInternal(link);

        if (folder.isDirectory())
        {
            String path = link.path;

            if (path.endsWith("/"))
            {
                path = path.substring(0, path.length() - 1);
            }

            getLinksFromPathRecursively(folder, links, link, path, recursive ? 9999 : 1);
        }
    }
}