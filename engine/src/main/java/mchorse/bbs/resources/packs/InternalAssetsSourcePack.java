package mchorse.bbs.resources.packs;

import mchorse.bbs.resources.ISourcePack;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InternalAssetsSourcePack implements ISourcePack
{
    private String prefix;
    private Class clazz;

    public InternalAssetsSourcePack()
    {
        this("assets", InternalAssetsSourcePack.class);
    }

    public InternalAssetsSourcePack(String prefix, Class clazz)
    {
        this.prefix = prefix;
        this.clazz = clazz;
    }

    @Override
    public String getPrefix()
    {
        return this.prefix;
    }

    @Override
    public boolean hasAsset(Link link)
    {
        return this.clazz.getClassLoader().getResource(this.prefix + "/" + link.path) != null;
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        return this.clazz.getClassLoader().getResourceAsStream(this.prefix + "/" + link.path);
    }

    @Override
    public File getFile(Link link)
    {
        return null;
    }

    @Override
    public Link getLink(File file)
    {
        return null;
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
        URL url = this.clazz.getProtectionDomain().getCodeSource().getLocation();

        try
        {
            File file = Paths.get(url.toURI()).toFile();

            if (file.isDirectory())
            {
                this.getLinksFromFolder(this.getResourcesFolder(file), link, links, recursive);
            }
            else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
            {
                this.getLinksFromZipFile(file, link, links, recursive);
            }
        }
        catch (Exception e)
        {}
    }

    /**
     * Get resources folder. In case this is run in development environment,
     * the project can be compiled to two folders "classes/" and "resources/."
     * To get the right folder, this method checks if the folder with
     * assets exists.
     */
    private File getResourcesFolder(File file)
    {
        if (new File(file, this.prefix).exists())
        {
            return file;
        }

        for (File subFile : file.getParentFile().listFiles())
        {
            if (new File(subFile, this.prefix).exists())
            {
                return subFile;
            }
        }

        return file;
    }

    private void getLinksFromFolder(File folder, Link link, Collection<Link> links, boolean recursive)
    {
        File file = new File(folder, this.prefix + "/" + link.path);

        ExternalAssetsSourcePack.getLinksFromPathRecursively(file, links, link, link.path, recursive ? 9999 : 1);
    }

    /* Zip handling */

    private void getLinksFromZipFile(File file, Link link, Collection<Link> links, boolean recursive)
    {
        try (ZipFile zipFile = new ZipFile(file))
        {
            this.handleLinksFromZipFile(link, zipFile, links, recursive);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handleLinksFromZipFile(Link link, ZipFile file, Collection<Link> links, boolean recursive)
    {
        Enumeration<? extends ZipEntry> it = file.entries();

        while (it.hasMoreElements())
        {
            String name = it.nextElement().getName();
            String assets = this.prefix + "/";
            String path = assets + link.path;

            if (name.startsWith(path))
            {
                String newPath = name.substring(assets.length());
                String linkPath = link.path;

                if (!link.path.endsWith("/"))
                {
                    linkPath += "/";
                }

                if (!recursive && StringUtils.countMatches(newPath.substring(linkPath.length()), "/") == 0)
                {
                    continue;
                }

                links.add(new Link(this.prefix, newPath));
            }
        }
    }
}