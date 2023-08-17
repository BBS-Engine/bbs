package mchorse.bbs.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface ISourcePack
{
    public String getPrefix();

    public boolean hasAsset(Link link);

    public InputStream getAsset(Link link) throws IOException;

    public File getFile(Link link);

    public Link getLink(File file);

    public void getLinksFromPath(Collection<Link> links, Link link, boolean recursive);
}