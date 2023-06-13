package mchorse.bbs.cubic.model;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.resources.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IModelLoader
{
    public static Link getLink(Link link, List<Link> links, String suffix)
    {
        return getLink(link, links, (l) -> l.path.endsWith(suffix));
    }

    public static Link getLink(Link link, List<Link> links, Predicate<Link> predicate)
    {
        if (!links.contains(link))
        {
            for (Link l : links)
            {
                if (predicate.test(l))
                {
                    return l;
                }
            }
        }

        return link;
    }

    public static List<Link> getLinks(List<Link> links, String suffix)
    {
        return getLinks(links, (l) -> l.path.endsWith(suffix));
    }

    public static List<Link> getLinks(List<Link> links, Predicate<Link> predicate)
    {
        List<Link> newLinks = new ArrayList<Link>();

        for (Link l : links)
        {
            if (predicate.test(l))
            {
                newLinks.add(l);
            }
        }

        return newLinks;
    }

    public CubicModel load(ModelManager models, Link model, List<Link> links) throws Exception;
}