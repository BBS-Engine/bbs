package mchorse.bbs.settings.values.base;

import java.util.List;

public interface IValue
{
    public String getId();

    public void notifyParent();

    public IValue getParent();

    public List<String> getPathSegments();

    public default String getPath()
    {
        return String.join(".", this.getPathSegments());
    }
}