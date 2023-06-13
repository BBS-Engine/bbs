package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;

public abstract class BaseTweenProperty <T> extends BaseProperty<T>
{
    public BaseTweenProperty(Form form, String key, T value)
    {
        super(form, key, value);
    }

    @Override
    public T get(float transition)
    {
        if (this.isTweening())
        {
            return this.getTweened(transition);
        }

        return super.get(transition);
    }

    protected abstract T getTweened(float transition);
}