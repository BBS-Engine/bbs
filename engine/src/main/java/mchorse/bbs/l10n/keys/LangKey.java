package mchorse.bbs.l10n.keys;

import mchorse.bbs.resources.Link;

public class LangKey implements IKey
{
    public LangKey reference;
    public String key;
    public String content;
    public boolean wasRequested;

    private Link origin;

    public LangKey(Link origin, String key, String content)
    {
        this.origin = origin;
        this.key = key;
        this.content = content;
    }

    @Override
    public String get()
    {
        return this.content;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    public void setOrigin(Link origin)
    {
        this.origin = origin;
    }

    public Link getOrigin()
    {
        return this.reference != null ? this.reference.origin : origin;
    }

    @Override
    public String toString()
    {
        return this.content;
    }
}