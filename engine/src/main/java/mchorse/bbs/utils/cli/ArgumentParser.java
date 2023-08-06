package mchorse.bbs.utils.cli;

import mchorse.bbs.data.DataParser;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.StringType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ArgumentParser
{
    public Map<String, Argument> arguments = new HashMap<>();

    public ArgumentParser register(String name, ArgumentType type)
    {
        return this.register(name, null, type);
    }

    public ArgumentParser register(String name, String alias, ArgumentType type)
    {
        Argument argument = new Argument(name, alias, type);

        this.arguments.put("--" + name, argument);

        if (alias != null && !alias.isEmpty())
        {
            this.arguments.put("-" + alias, argument);
        }

        return this;
    }

    public MapType parse(String[] arguments)
    {
        MapType output = new MapType();
        String key = null;
        StringJoiner builder = new StringJoiner(" ");

        for (int i = 0; i < arguments.length; i++)
        {
            String string = arguments[i];

            if (string.startsWith("-"))
            {
                this.pushArgument(output, key, builder.toString());

                key = string;
                builder = new StringJoiner(" ");
            }
            else
            {
                builder.add(string);
            }
        }

        if (key != null)
        {
            this.pushArgument(output, key, builder.toString());
        }

        return output;
    }

    private void pushArgument(MapType map, String key, String stringValue)
    {
        Argument argument = this.arguments.get(key);
        BaseType value = this.getValue(key, stringValue);

        if (argument != null && value != null)
        {
            map.put(argument.name, value);
        }
    }

    private String getKey(String key)
    {
        Argument argument = this.arguments.get(key);

        if (argument != null)
        {
            return argument.name;
        }

        return null;
    }

    private BaseType getValue(String key, String string)
    {
        Argument argument = this.arguments.get(key);

        if (argument != null)
        {
            switch (argument.type)
            {
                case NUMBER:
                    if (string.trim().isEmpty())
                    {
                        return new ByteType(true);
                    }

                    return DataParser.parseNumeric(string);

                case PATH:
                    if (!new File(string).exists())
                    {
                        return null;
                    }

                    break;
            }
        }

        return new StringType(string);
    }
}