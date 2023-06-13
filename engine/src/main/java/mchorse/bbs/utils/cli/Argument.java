package mchorse.bbs.utils.cli;

public class Argument
{
    public String name;
    public String alias;
    public ArgumentType type;

    public Argument(String name, String alias, ArgumentType type)
    {
        this.name = name;
        this.alias = alias;
        this.type = type;
    }
}