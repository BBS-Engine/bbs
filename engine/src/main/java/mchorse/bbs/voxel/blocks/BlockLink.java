package mchorse.bbs.voxel.blocks;

import mchorse.bbs.resources.Link;

public class BlockLink extends Link
{
    public static final String VARIANT_SEPARATOR = "#";

    public final int variant;

    public static BlockLink create(String full)
    {
        Link link = Link.create(full);
        String path = link.path;
        int colon = path.indexOf(VARIANT_SEPARATOR);
        int variant = 0;

        if (colon >= 0)
        {
            try
            {
                variant = Integer.parseInt(path.substring(colon + 1));
            }
            catch (Exception e)
            {}

            path = path.substring(0, colon);
        }

        return new BlockLink(link.source, path, variant);
    }

    public BlockLink(Link link, int variant)
    {
        this(link.source, link.path, variant);
    }

    public BlockLink(String source, String path, int variant)
    {
        super(source, path);

        this.variant = variant;
    }

    public BlockLink(String source, String path)
    {
        this(source, path, 0);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BlockLink)
        {
            BlockLink blockLink = (BlockLink) obj;

            result = result && this.variant == blockLink.variant;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return super.toString() + VARIANT_SEPARATOR + this.variant;
    }
}