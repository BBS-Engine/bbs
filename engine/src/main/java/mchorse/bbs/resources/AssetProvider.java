package mchorse.bbs.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetProvider
{
    private Map<String, List<ISourcePack>> sourcePacks = new HashMap<>();

    public void registerFirst(ISourcePack pack)
    {
        this.sourcePacks.computeIfAbsent(pack.getPrefix(), (k) -> new ArrayList<>()).add(0, pack);
    }

    public void register(ISourcePack pack)
    {
        this.sourcePacks.computeIfAbsent(pack.getPrefix(), (k) -> new ArrayList<>()).add(pack);
    }

    public Collection<String> getSourceKeys()
    {
        return this.sourcePacks.keySet();
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

    public Collection<Link> getLinksFromPath(Link link)
    {
        return this.getLinksFromPath(link, true);
    }

    public Collection<Link> getLinksFromPath(Link link, boolean recursive)
    {
        Set<Link> links = new HashSet<>();
        List<ISourcePack> packs = this.getPacks(link.source);

        for (ISourcePack pack : packs)
        {
            pack.getLinksFromPath(links, link, recursive);
        }

        return links;
    }
}