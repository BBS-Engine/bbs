package mchorse.bbs.utils.resources;

import mchorse.bbs.resources.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiLinkManager
{
    private static int id = 0;
    private static Map<Link, List<Pair>> map = new HashMap<Link, List<Pair>>();

    public static int getId(MultiLink location)
    {
        if (location.children.isEmpty())
        {
            return -1;
        }

        Link keyRL = location.children.get(0).path;
        List<Pair> pairs = map.computeIfAbsent(keyRL, (k) -> new ArrayList<Pair>());

        for (Pair pair : pairs)
        {
            if (pair.link.equals(location))
            {
                return pair.id;
            }
        }

        int newId = id;

        pairs.add(new Pair(newId, (MultiLink) location.copy()));
        id += 1;

        return newId;
    }

    private static class Pair
    {
        public int id;
        public MultiLink link;

        public Pair(int id, MultiLink link)
        {
            this.id = id;
            this.link = link;
        }
    }
}