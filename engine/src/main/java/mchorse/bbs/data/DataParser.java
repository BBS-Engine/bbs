package mchorse.bbs.data;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.DoubleType;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.LongType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.ShortType;
import mchorse.bbs.data.types.StringType;

/**
 * Data parser
 *
 * Parses string representation of any of base types
 */
public class DataParser
{
    private String string;
    private int index;

    public static BaseType parse(String string)
    {
        string = string.trim();

        if (string.isEmpty())
        {
            return null;
        }

        char first = string.charAt(0);
        int size = string.length();
        boolean isMap = first == '{';

        if (isMap || first == '[')
        {
            char opposite = isMap ? '}' : ']';
            String excerpt = string.substring(1, string.lastIndexOf(opposite));

            return isMap ? parseMap(excerpt) : parseList(excerpt);
        }
        else if (first == '"')
        {
            char previous = first;

            for (int i = 1; i < size; i++)
            {
                char current = string.charAt(i);

                if (current == '"' && (previous != '\\' || i == size -1))
                {
                    return new StringType(DataToString.unescape(string.substring(1, i)));
                }

                previous = current;
            }
        }

        return parseNumeric(string, size);
    }

    public static BaseType parseNumeric(String string)
    {
        return parseNumeric(string, string.length());
    }

    public static BaseType parseNumeric(String string, int size)
    {
        if (string.equals("null"))
        {
            return new ByteType((byte) 0);
        }

        boolean aTrue = string.equalsIgnoreCase("true");

        if (aTrue || string.equalsIgnoreCase("false"))
        {
            return new ByteType(aTrue);
        }

        char last = string.charAt(size - 1);

        try
        {
            if (last == 'b' || last == 'B')
            {
                return new ByteType((byte) Integer.parseInt(string.substring(0, size - 1)));
            }
            else if (last == 's' || last == 'S')
            {
                return new ShortType((short) Integer.parseInt(string.substring(0, size - 1)));
            }
            else if (last == 'f' || last == 'F')
            {
                return new FloatType(Float.parseFloat(string.substring(0, size - 1)));
            }
            else if (last == 'l' || last == 'L')
            {
                return new LongType(Long.parseLong(string.substring(0, size - 1)));
            }
            else if (last == 'd' || last == 'D')
            {
                return new DoubleType(Double.parseDouble(string.substring(0, size - 1)));
            }
            else if (Character.isDigit(last) && string.contains("."))
            {
                return new DoubleType(Double.parseDouble(string));
            }

            return new IntType(Integer.parseInt(string));
        }
        catch (Exception e)
        {}

        return null;
    }

    private static BaseType parseMap(String string)
    {
        MapType map = new MapType();

        if (string.trim().isEmpty())
        {
            return map;
        }

        DataParser parser = new DataParser(string);

        while (true)
        {
            String key = parser.parseKey();
            String value = parser.parseValue();

            if (value == null)
            {
                break;
            }

            BaseType type = parse(value);

            if (type == null)
            {
                throw new IllegalStateException("Something went wrong with parsing a map: " + value);
            }

            map.put(key.trim(), type);
        }

        return map;
    }

    private static BaseType parseList(String string)
    {
        ListType list = new ListType();

        if (string.trim().isEmpty())
        {
            return list;
        }

        DataParser parser = new DataParser(string);

        while (true)
        {
            String value = parser.parseValue();

            if (value == null)
            {
                break;
            }

            BaseType type = parse(value);

            if (type == null)
            {
                throw new IllegalStateException("Something went wrong with parsing a list: " + value);
            }

            list.add(type);
        }

        return list;
    }

    public DataParser(String string)
    {
        this(string, 0);
    }

    public DataParser(String string, int index)
    {
        this.string = string;
        this.index = index;
    }

    public String parseKey()
    {
        String s = this.parseUntil(':');

        if (s != null)
        {
            s = s.trim();

            if (s.startsWith("\"") && s.endsWith("\""))
            {
                s = DataToString.unescape(s.substring(1, s.length() - 1));
            }
        }

        return s;
    }

    public String parseValue()
    {
        return this.parseUntil(',');
    }

    public String parseUntil(char stop)
    {
        int size = this.string.length();

        if (this.index >= size)
        {
            return null;
        }

        int nesting = 0;
        boolean string = false;
        char previous = '\0';

        for (int i = this.index; i < size; i++)
        {
            char current = this.string.charAt(i);

            if (current == '"')
            {
                if (string && previous == '\\')
                {
                    previous = current;

                    continue;
                }

                string = !string;
            }

            if (string)
            {
                if (current == '\\' && previous == '\\')
                {
                    previous = '\0';
                }
                else
                {
                    previous = current;
                }

                continue;
            }

            if (current == '{' || current == '[')
            {
                nesting += 1;
            }
            else if (current == '}' || current == ']')
            {
                nesting -= 1;
            }

            if (nesting == 0 && current == stop)
            {
                return this.setAndReturn(i, 1);
            }

            previous = current;
        }

        return this.setAndReturn(size, 0);
    }

    private String setAndReturn(int i, int offset)
    {
        String result = this.string.substring(this.index, i);

        this.index = i + offset;

        return result;
    }
}