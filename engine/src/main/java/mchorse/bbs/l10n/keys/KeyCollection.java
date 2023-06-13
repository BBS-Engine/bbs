package mchorse.bbs.l10n.keys;

import mchorse.bbs.BBS;
import mchorse.bbs.resources.Link;

import java.util.Collection;

/**
 * Key collection allows to force load a set of keys given the
 * template for ID. ^ symbol is the placeholder symbol.
 */
public class KeyCollection
{
    public String id;

    public KeyCollection(String id)
    {
        this.id = id;
    }

    private String getKey(String relativeKey)
    {
        return this.id.replace("^", relativeKey);
    }

    public KeyCollection load(Collection<String> keys)
    {
        for (String key : keys)
        {
            this.get(key);
        }

        return this;
    }

    public IKey get(Enum e)
    {
        return this.get(e.name().toLowerCase());
    }

    public IKey get(Link key)
    {
        return this.get(key.toString());
    }

    public IKey get(String relativeKey)
    {
        return BBS.getL10n().getKey(this.getKey(relativeKey));
    }
}