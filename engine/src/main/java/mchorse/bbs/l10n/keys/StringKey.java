package mchorse.bbs.l10n.keys;

public class StringKey implements IKey
{
    public String string;

    public StringKey(String string)
    {
        this.string = string;
    }

    @Override
    public String get()
    {
        return this.string;
    }

    @Override
    public String toString()
    {
        return this.string;
    }
}