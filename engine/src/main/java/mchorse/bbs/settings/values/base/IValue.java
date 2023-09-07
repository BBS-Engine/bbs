package mchorse.bbs.settings.values.base;

import java.util.List;

public interface IValue
{
    public String getId();

    public void preNotifyParent(IValue value);

    public void postNotifyParent(IValue value);

    public IValue getParent();

    public List<String> getPathSegments();

    public default String getPath()
    {
        return String.join(".", this.getPathSegments());
    }
}