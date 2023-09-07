package mchorse.bbs.settings.values.base;

public abstract class BaseValueBasic <T> extends BaseValue
{
    protected T value;

    public BaseValueBasic(String id, T value)
    {
        super(id);

        this.value = value;
    }

    public T get()
    {
        return this.value;
    }

    public void set(T value)
    {
        this.preNotifyParent(this);

        this.value = value;

        this.postNotifyParent(this);
    }
}