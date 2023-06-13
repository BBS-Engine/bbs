package mchorse.bbs.data;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Data to JSON-like string utility
 *
 * This class is responsible for reading and writing to JSON-like
 * format with indentation.
 */
public class DataToString
{
    public static String unescape(String string)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0, c = string.length(); i < c; i++)
        {
            char character = string.charAt(i);

            if (character == '\\' && i + 1 < c)
            {
                char next = string.charAt(i + 1);

                if (next == '\\')
                {
                    i += 1;
                }
                else if (next == '\"')
                {
                    builder.append('"');
                    i += 1;

                    continue;
                }
                else if (next == 'n')
                {
                    builder.append('\n');
                    i += 1;

                    continue;
                }
                else if (next == 'u' && i + 5 < c)
                {
                    char[] chars = {string.charAt(i + 2), string.charAt(i + 3), string.charAt(i + 4), string.charAt(i + 5)};

                    builder.append((char) StringUtils.parseHex(chars));
                    i += 5;

                    continue;
                }
            }

            builder.append(character);
        }

        return builder.toString();
    }

    public static String escape(String string)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0, c = string.length(); i < c; i++)
        {
            char character = string.charAt(i);

            if (character == '"')
            {
                builder.append('\\');
            }
            else if (character == '\n')
            {
                builder.append("\\n");

                continue;
            }
            else if (character == '\\')
            {
                builder.append('\\');
            }

            builder.append(character);
        }

        return builder.toString();
    }

    public static String escapeQuoted(String string)
    {
        return "\"" + escape(string) + "\"";
    }

    public static String toString(BaseType base)
    {
        return toString(base, false);
    }

    public static String toString(BaseType base, boolean jsonLike)
    {
        DataStringifier stringifier = new DataStringifier();

        if (jsonLike)
        {
            stringifier.jsonLike();
        }

        return stringifier.toString(base);
    }

    public static MapType mapFromString(String string)
    {
        BaseType data = fromString(string);

        return data instanceof MapType ? (MapType) data : null;
    }

    public static ListType listFromString(String string)
    {
        BaseType data = fromString(string);

        return data instanceof ListType ? (ListType) data : null;
    }

    public static BaseType fromString(String string)
    {
        if (string == null)
        {
            return null;
        }

        try
        {
            return DataParser.parse(string);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static void write(File file, BaseType type) throws IOException
    {
        write(file, type, false);
    }

    public static void write(File file, BaseType type, boolean jsonLike) throws IOException
    {
        IOUtils.writeText(file, toString(type, jsonLike));
    }

    public static boolean writeSilently(File file, BaseType type)
    {
        return writeSilently(file, type, false);
    }

    public static boolean writeSilently(File file, BaseType type, boolean jsonLike)
    {
        try
        {
            IOUtils.writeText(file, toString(type, jsonLike));

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static BaseType read(File file) throws IOException
    {
        return fromString(IOUtils.readText(file));
    }
}