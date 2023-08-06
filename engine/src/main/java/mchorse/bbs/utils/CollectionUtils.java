package mchorse.bbs.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils
{
    public static <T> Set<T> setOf(T... values)
    {
        Set<T> set = new HashSet<>();

        for (T value : values)
        {
            set.add(value);
        }

        return set;
    }

    public static boolean inRange(Collection collection, int index)
    {
        return index >= 0 && index < collection.size();
    }
}