package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;

import java.util.Objects;

public abstract class BaseProperty <T> implements IFormProperty<T>
{
    protected Form form;
    protected String key;
    protected T value;
    protected T lastValue;

    private boolean tweening = true;
    protected int ticks = -1;
    protected int duration;
    protected IInterpolation interpolation = Interpolation.LINEAR;

    public BaseProperty(Form form, String key, T value)
    {
        this.form = form;
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public Form getForm()
    {
        return this.form;
    }

    @Override
    public void set(T value)
    {
        this.value = value;

        this.ticks = -1;
    }

    @Override
    public T get()
    {
        return this.value;
    }

    @Override
    public T get(float transition)
    {
        return this.value;
    }

    @Override
    public void update()
    {
        if (this.ticks >= 0)
        {
            this.ticks -= 1;
        }
    }

    @Override
    public void tween(T newValue, int duration, IInterpolation interpolation)
    {
        this.lastValue = this.value;
        this.value = newValue;

        this.ticks = this.duration = duration;
        this.interpolation = interpolation == null ? Interpolation.LINEAR : interpolation;
        this.tweening = true;
    }

    @Override
    public void pause(int offset)
    {
        this.ticks = offset;
        this.tweening = false;
    }

    @Override
    public boolean isTweening()
    {
        return this.ticks > 0;
    }

    @Override
    public float getTweenFactor(float transition)
    {
        if (!this.isTweening())
        {
            return 1;
        }

        return 1 - (this.ticks - (this.tweening ? transition : 0)) / (float) this.duration;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof BaseProperty)
        {
            BaseProperty baseValue = (BaseProperty) obj;

            return Objects.equals(this.value, baseValue.value);
        }

        return false;
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has(this.getKey()))
        {
            this.propertyFromData(data, this.getKey());
        }
    }

    protected abstract void propertyFromData(MapType data, String key);
}