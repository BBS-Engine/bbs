package mchorse.bbs.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetProvider
{
    private Map<String, List<ISourcePack>> sourcePacks = new HashMap<String, List<ISourcePack>>();

    public void registerFirst(ISourcePack pack)
    {
        this.sourcePacks.computeIfAbsent(pack.getPrefix(), (k) -> new ArrayList<ISourcePack>()).add(0, pack);
    }

    public void register(ISourcePack pack)
    {
        this.sourcePacks.computeIfAbsent(pack.getPrefix(), (k) -> new ArrayList<ISourcePack>()).add(pack);
    }

    private List<ISourcePack> getPacks(String source)
    {
        List<ISourcePack> sourcePacks = this.sourcePacks.get(source);

        return sourcePacks == null ? Collections.emptyList() : sourcePacks;
    }

    public InputStream getAsset(Link link) throws IOException
    {
        List<ISourcePack> packs = this.getPacks(link.source);

        for (ISourcePack pack : packs)
        {
            if (pack.hasAsset(link))
            {
                return pack.getAsset(link);
            }
        }

        throw new FileNotFoundException("Asset " + link + " couldn't be found!");
    }

    public File getFile(Link link)
    {
        List<ISourcePack> packs = this.getPacks(link.source);

        for (ISourcePack pack : packs)
        {
            File file = pack.getFile(link);

            if (file != null)
            {
                return file;
            }
        }

        return null;
    }

    public List<Link> getLinksFromPath(Link link)
    {
        return this.getLinksFromPath(link, true);
    }

    public List<Link> getLinksFromPath(Link link, boolean recursive)
    {
        List<Link> links = new ArrayList<Link>();
        List<ISourcePack> packs = this.getPacks(link.source);

        for (ISourcePack pack : packs)
        {
            pack.getLinksFromPath(links, link, recursive);
        }

        return links;
    }
}