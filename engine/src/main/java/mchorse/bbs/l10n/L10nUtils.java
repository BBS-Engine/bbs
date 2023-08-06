package mchorse.bbs.l10n;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.l10n.keys.LangKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L10nUtils
{
    public static final Link LAZY = Link.assets("lazy.json");

    public static String analyzeStrings(L10n l10n)
    {
        Map<String, LangKey> strings = l10n.getStrings();
        StringBuilder builder = new StringBuilder();

        /* Calculate per language file count of strings */
        Map<Link, Integer> perFile = new HashMap<>();

        for (LangKey value : strings.values())
        {
            Link origin = value.getOrigin();

            if (origin == null)
            {
                continue;
            }

            int count = perFile.computeIfAbsent(origin, (k) -> 0);

            perFile.put(origin, count + 1);
        }

        if (!perFile.isEmpty())
        {
            builder.append("Language strings per file:\n");

            for (Map.Entry<Link, Integer> entry : perFile.entrySet())
            {
                builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        /* Calculate missing and surplus strings */
        List<LangKey> missing = new ArrayList<>();
        List<LangKey> surplus = new ArrayList<>();

        for (LangKey key : strings.values())
        {
            if (key.getOrigin() == null)
            {
                missing.add(key);
            }
            else if (!key.wasRequested)
            {
                surplus.add(key);
            }
        }

        sortList(missing);
        sortList(surplus);

        if (!missing.isEmpty())
        {
            builder.append("\nMissing strings:\n");

            for (LangKey key : missing)
            {
                builder.append("- ").append(key.content);

                if (!key.key.isEmpty())
                {
                    builder.append(" - ").append(key.key);
                }

                builder.append("\n");
            }
        }

        if (!surplus.isEmpty())
        {
            builder.append("\nSurplus strings:\n");

            for (LangKey key : surplus)
            {
                builder.append("- ").append(key.content);

                if (!key.key.isEmpty())
                {
                    builder.append(" - ").append(key.key);
                }

                builder.append("\n");
            }
        }

        /* Misc */
        builder.append("\nTotal language keys: ").append(strings.size());

        return builder.toString();
    }

    public static void compile(File export, Map<String, LangKey> strings)
    {
        Map<Link, List<LangKey>> keysPerFile = new HashMap<>();

        export.mkdirs();

        for (LangKey key : strings.values())
        {
            Link origin = key.getOrigin() == null ? LAZY : key.getOrigin();

            keysPerFile.computeIfAbsent(origin, (k) -> new ArrayList<>()).add(key);
        }

        for (List<LangKey> list : keysPerFile.values())
        {
            sortList(list);
        }

        for (Map.Entry<Link, List<LangKey>> entry : keysPerFile.entrySet())
        {
            Link key = entry.getKey();
            File file = new File(export, "lang." + key.source + "_" + key.path.replaceAll("/", "."));
            MapType out = new MapType(false);

            for (LangKey k : entry.getValue())
            {
                out.putString(k.key, k.content);
            }

            DataToString.writeSilently(file, out, true);
        }
    }

    public static void sortList(List<LangKey> list)
    {
        Map<String, List<LangKey>> prefixes = new HashMap<>();
        List<String> ordered = new ArrayList<>();

        for (LangKey key : list)
        {
            String k = key.key;
            int endIndex = k.lastIndexOf('.');

            String prefix = endIndex == -1 ? k : k.substring(0, endIndex);
            List<LangKey> keys = prefixes.get(prefix);

            if (keys == null)
            {
                keys = new ArrayList<>();

                prefixes.put(prefix, keys);
                ordered.add(prefix);
            }

            keys.add(key);
        }

        for (List<LangKey> keys : prefixes.values())
        {
            keys.sort(Comparator.comparing(a -> a.key));
        }

        ordered.sort((a, b) ->
        {
            String[] aSplits = a.split("\\.");
            String[] bSplits = b.split("\\.");

            for (int i = 0, c = Math.min(aSplits.length, bSplits.length); i < c; i++)
            {
                int diff = aSplits[i].compareTo(bSplits[i]);

                if (diff != 0)
                {
                    return diff;
                }
            }

            return a.compareTo(b);
        });

        list.clear();

        for (String prefix : ordered)
        {
            list.addAll(prefixes.get(prefix));
        }
    }

    public static List<Pair<String, String>> readAdditionalLanguages(File file)
    {
        if (file.isFile())
        {
            try
            {
                MapType mapType = DataToString.mapFromString(IOUtils.readText(file));
                List<Pair<String, String>> additionalLanguages = new ArrayList<>();

                for (String key : mapType.keys())
                {
                    additionalLanguages.add(new Pair<>(mapType.getString(key), key));
                }

                return additionalLanguages;
            }
            catch (Exception e)
            {}
        }

        return Collections.emptyList();
    }
}