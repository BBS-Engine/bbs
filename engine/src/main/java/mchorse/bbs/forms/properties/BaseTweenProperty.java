package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;

public abstract class BaseTweenProperty <T> extends BaseProperty<T>
{
    private final IGenericKeyframeFactory<T> factory;

    public BaseTweenProperty(Form form, String key, T value, IGenericKeyframeFactory<T> factory)
    {
        super(form, key, value);

        this.factory = factory;
    }

    public IGenericKeyframeFactory<T> getFactory()
    {
        return this.factory;
    }

    @Override
    public T get(float transition)
    {
        if (this.isTweening())
        {
            return this.factory.interpolate(this.lastValue, this.value, this.interpolation, this.getTweenFactor(transition));
        }

        return super.get(transition);
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel(String key)
    {
        return new GenericKeyframeChannel(key, this.factory);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(this.factory.fromData(data.get(key)));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), this.factory.toData(this.value));
    }
}