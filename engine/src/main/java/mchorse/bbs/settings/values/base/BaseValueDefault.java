package mchorse.bbs.settings.values.base;

public abstract class BaseValueDefault <T> extends BaseValueBasic<T>
{
    protected T defaultValue;

    public BaseValueDefault(String id, T defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;
    }
}
