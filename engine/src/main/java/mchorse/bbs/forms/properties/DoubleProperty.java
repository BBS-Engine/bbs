package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

public class DoubleProperty extends BaseTweenProperty<Double>
{
    public DoubleProperty(Form form, String key, Double value)
    {
        super(form, key, value);
    }

    @Override
    protected Double getTweened(float transition)
    {
        return this.interpolation.interpolate(this.value, this.lastValue, this.getTweenFactor(transition));
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getDouble(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putDouble(this.getKey(), this.value);
    }
}