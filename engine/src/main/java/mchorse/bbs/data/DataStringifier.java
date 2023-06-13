package mchorse.bbs.data;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.NumericType;

public class DataStringifier
{
    public boolean wrapKeysInQuotes;
    public boolean numericTypes = true;
    public boolean keywordBooleans;
    public String indent = "    ";

    public void jsonLike()
    {
        this.wrapKeysInQuotes = true;
        this.numericTypes = false;
        this.keywordBooleans = true;
    }

    public String toString(BaseType base)
    {
        return this.toString(base, new StringBuilder(), "").toString();
    }

    protected StringBuilder toString(BaseType base, StringBuilder builder, String indent)
    {
        if (base instanceof ListType)
        {
            this.listToString(builder, indent, (ListType) base);
        }
        else if (base instanceof MapType)
        {
            this.mapToString(builder, indent, (MapType) base);
        }
        else
        {
            if (this.keywordBooleans && base instanceof ByteType)
            {
                builder.append(((ByteType) base).value == 0 ? "false" : "true");
            }
            else if (!this.numericTypes && base instanceof NumericType)
            {
                String string = base.toString();
                int lastIndex = string.length() - 1;
                char last = string.charAt(lastIndex);

                if (!Character.isDigit(last))
                {
                    string = string.substring(0, lastIndex);
                }

                builder.append(string);
            }
            else
            {
                builder.append(base.toString());
            }
        }

        return builder;
    }

    public void mapToString(StringBuilder builder, String indent, MapType map)
    {
        builder.append("{");

        if (!map.isEmpty())
        {
            this.writeIndent(builder, "\n");
        }

        int i = 0;

        for (String key : map.elements.keySet())
        {
            this.writeIndent(builder, indent);
            this.writeIndent(builder, this.indent);

            if (this.wrapKeysInQuotes)
            {
                builder.append(DataToString.escapeQuoted(key));
            }
            else
            {
                builder.append(key);
            }

            builder.append(": ");

            toString(map.get(key), builder, indent + this.indent);

            if (i < map.size() - 1)
            {
                builder.append(",");
            }

            this.writeIndent(builder, "\n");

            i++;
        }

        if (!map.isEmpty())
        {
            this.writeIndent(builder, indent);
        }

        builder.append("}");
    }

    public void listToString(StringBuilder builder, String indent, ListType list)
    {
        builder.append('[');

        boolean compact = this.isCompactList(list);

        if (!compact)
        {
            this.writeIndent(builder, "\n");
        }

        for (int i = 0; i < list.size(); i++)
        {
            if (!compact)
            {
                this.writeIndent(builder, indent);
                this.writeIndent(builder, this.indent);
            }

            this.toString(list.get(i), builder, indent + this.indent);

            if (i < list.size() - 1)
            {
                builder.append(',');

                if (compact)
                {
                    builder.append(' ');
                }
            }

            if (!compact)
            {
                this.writeIndent(builder, "\n");
            }
        }

        if (!compact)
        {
            this.writeIndent(builder, indent);
        }

        builder.append(']');
    }

    public boolean isCompactList(ListType list)
    {
        for (BaseType data : list)
        {
            if (!BaseType.isPrimitive(data))
            {
                return false;
            }
        }

        return true;
    }

    protected void writeIndent(StringBuilder builder, String s)
    {
        if (!this.indent.isEmpty())
        {
            builder.append(s);
        }
    }
}
