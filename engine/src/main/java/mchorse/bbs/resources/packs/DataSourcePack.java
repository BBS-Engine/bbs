package mchorse.bbs.resources.packs;

import mchorse.bbs.data.storage.DataURLStorage;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteArrayType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.ISourcePack;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.DataPath;
import mchorse.bbs.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSourcePack implements ISourcePack
{
    private Map<Link, byte[]> resources = new HashMap<>();
    private Set<Link> keys = new HashSet<>();

    private URL url;

    /**
     * Pack given folder into a map type (as byte arrays) to be later used
     * with {@link DataSourcePack}.
     */
    public static MapType pack(File folder)
    {
        MapType map = new MapType();

        readRecursively(map, folder, null);

        return map;
    }

    private static void readRecursively(MapType data, File assets, String prefix)
    {
        for (File file : assets.listFiles())
        {
            String name = file.getName();

            if (file.isDirectory())
            {
                readRecursively(data, file, prefix == null ? name : prefix + "/" + name);
            }
            else
            {
                read(data, file, prefix + "/" + name);
            }
        }
    }

    private static void read(MapType data, File file, String path)
    {
        try
        {
            data.putByteArray(path, IOUtils.readBytes(new FileInputStream(file)));
        }
        catch (Exception e)
        {
            System.err.println("Failed to read or write " + path + "!");
            e.printStackTrace();
        }
    }

    public DataSourcePack(URL url)
    {
        this.url = url;

        this.read(url);

        this.keys.addAll(this.resources.keySet());
    }

    private void read(URL url)
    {
        BaseType data = new DataURLStorage(url).readSilently();

        if (data == null || !data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (String key : map.keys())
        {
            BaseType resource = map.get(key);

            if (!BaseType.is(resource, BaseType.TYPE_BYTE_ARRAY))
            {
                continue;
            }

            this.resources.put(Link.assets(key), ((ByteArrayType) resource).value);
        }
    }

    @Override
    public String getPrefix()
    {
        return "assets";
    }

    @Override
    public boolean hasAsset(Link link)
    {
        return this.keys.contains(link);
    }

    @Override
    public InputStream getAsset(Link link) throws IOException
    {
        byte[] bytes = this.resources.get(link);

        if (bytes == null)
        {
            this.read(this.url);

            bytes = this.resources.get(link);
        }

        /* Free the memory when that data is no longer needed */
        if (bytes != null)
        {
            this.resources.put(link, null);
        }

        return new ByteArrayInputStream(bytes);
    }

    @Override
    public File getFile(Link link)
    {
        return null;
    }

    @Override
    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive)
    {
        DataPath linkPath = new DataPath(link.path);

        for (Link key : this.keys)
        {
            if (!key.source.equals(link.source))
            {
                continue;
            }

            DataPath keyPath = new DataPath(key.path);
            boolean startsWith = keyPath.startsWith(linkPath);

            if (!recursive && startsWith)
            {
                if (keyPath.size() - linkPath.size() == 1)
                {
                    links.add(new Link(key.source, keyPath.toString()));
                }
                else
                {
                    links.add(new Link(key.source, keyPath.getTo(linkPath.size() + 1) + "/"));
                }
            }
            else if (recursive && startsWith)
            {
                links.add(key);

                keyPath = keyPath.getParent();

                while (keyPath.size() > linkPath.size())
                {
                    links.add(new Link(key.source, keyPath + "/"));

                    keyPath = keyPath.getParent();
                }
            }
        }
    }
}