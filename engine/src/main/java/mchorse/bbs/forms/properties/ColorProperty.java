package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.serializers.KeyframeSerializers;

public class ColorProperty extends BaseTweenProperty<Color>
{
    private Color i = new Color();

    public ColorProperty(Form form, String key, Color value)
    {
        super(form, key, value);
    }

    @Override
    protected Color getTweened(float transition)
    {
        float factor = this.getTweenFactor(transition);

        this.i.r = this.interpolation.interpolate(this.lastValue.r, this.value.r, factor);
        this.i.g = this.interpolation.interpolate(this.lastValue.g, this.value.g, factor);
        this.i.b = this.interpolation.interpolate(this.lastValue.b, this.value.b, factor);
        this.i.a = this.interpolation.interpolate(this.lastValue.a, this.value.a, factor);

        return this.i;
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(new Color().set(data.getInt(key)));
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt(this.getKey(), this.value.getARGBColor());
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel()
    {
        return new GenericKeyframeChannel(KeyframeSerializers.COLOR);
    }
}