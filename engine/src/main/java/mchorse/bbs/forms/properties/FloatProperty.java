package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class FloatProperty extends BaseTweenProperty<Float>
{
    public FloatProperty(Form form, String key, Float value)
    {
        super(form, key, value);
    }

    @Override
    protected Float getTweened(float transition)
    {
        return this.interpolation.interpolate(this.lastValue, this.value, this.getTweenFactor(transition));
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getFloat(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putFloat(this.getKey(), this.value);
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel()
    {
        return new GenericKeyframeChannel(KeyframeFactories.FLOAT);
    }
}