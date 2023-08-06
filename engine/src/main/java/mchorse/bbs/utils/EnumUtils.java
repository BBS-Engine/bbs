package mchorse.bbs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EnumUtils
{
    public static <T> T getValue(int ordinal, T[] values, T defaultValue)
    {
        if (ordinal < 0 || ordinal >= values.length)
        {
            return defaultValue;
        }

        return values[ordinal];
    }

    public static List<String> getKeys(Class<? extends Enum> clazz)
    {
        List<String> keys = new ArrayList<>();

        for (Enum e : clazz.getEnumConstants())
        {
            keys.add(e.name().toLowerCase());
        }

        return keys;
    }

    public static <T> List<String> getKeys(Class<T> clazz, Function<T, String> function)
    {
        List<String> keys = new ArrayList<>();

        if (function == null)
        {
            return keys;
        }

        for (T e : clazz.getEnumConstants())
        {
             keys.add(function.apply(e));
        }

        return keys;
    }
}