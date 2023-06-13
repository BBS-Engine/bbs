package mchorse.bbs.utils.resources;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.resources.Link;

import java.io.IOException;

public class LinkUtils
{
    private static final Link MULTILINK_PLACEHOLDER = Link.assets("textures/placeholder.png");

    /**
     * Get stream for multi resource location 
     */
    public static Pixels getStreamForMultiLink(MultiLink multi) throws IOException
    {
        if (multi.children.isEmpty())
        {
            throw new IOException("Given MultiLink is empty!");
        }

        try
        {
            if (BBSSettings.multiskinMultiThreaded.get())
            {
                MultiLinkThread.add(multi);

                return Pixels.fromPNGStream(BBS.getProvider().getAsset(MULTILINK_PLACEHOLDER));
            }
            else
            {
                MultiLinkThread.clear();

                return TextureProcessor.process(multi);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    public static Link create(String path)
    {
        return Link.create(path);
    }

    public static Link create(String domain, String path)
    {
        return new Link(domain, path);
    }

    public static Link create(BaseType data)
    {
        Link location = MultiLink.from(data);

        if (location != null)
        {
            return location;
        }

        if (BaseType.isString(data))
        {
            return create(((StringType) data).value);
        }

        return null;
    }

    public static BaseType toData(Link link)
    {
        if (link instanceof IWritableLink)
        {
            return ((IWritableLink) link).toData();
        }
        else if (link != null)
        {
            return new StringType(link.toString());
        }

        return null;
    }

    public static Link copy(Link link)
    {
        if (link instanceof IWritableLink)
        {
            return ((IWritableLink) link).copy();
        }
        else if (link != null)
        {
            return create(link.toString());
        }

        return null;
    }
}