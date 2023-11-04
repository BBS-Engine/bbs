package mchorse.bbs.l10n.keys;

import mchorse.bbs.BBS;

import java.util.List;

public interface IKey
{
    public static final IKey EMPTY = new StringKey("");

    public static IKey lang(String key)
    {
        return BBS.getL10n().getKey(key);
    }

    public static IKey lang(String key, String content, IKey reference)
    {
        LangKey langKey = BBS.getL10n().getKey(key, content);

        if (reference instanceof LangKey)
        {
            langKey.reference = (LangKey) reference;
        }

        return langKey;
    }

    /**
     * This method is used to create an IKey that contains raw string data.
     */
    public static IKey raw(String string)
    {
        return new StringKey(string);
    }

    public static IKey comp(List<IKey> keys)
    {
        return new CompoundKey(keys);
    }

    public String get();

    public default IKey format(Object... args)
    {
        return new FormatKey(this, args);
    }
}