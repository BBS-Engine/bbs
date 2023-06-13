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

    public static IKey str(String string)
    {
        return new StringKey(string);
    }

    public static IKey comp(List<IKey> keys)
    {
        return new CompoundKey(keys);
    }

    public String get();

    public String getKey();

    public default IKey format(Object... args)
    {
        return new FormatKey(this, args);
    }

    public default String formatString(Object... args)
    {
        return String.format(this.get(), args);
    }
}