package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

public class IntegerProperty extends BaseTweenProperty<Integer>
{
    public IntegerProperty(Form form, String key, Integer value)
    {
        super(form, key, value);
    }

    @Override
    protected Integer getTweened(float transition)
    {
        return (int) this.interpolation.interpolate(this.lastValue, this.value, this.getTweenFactor(transition));
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getInt(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt(this.getKey(), this.value);
    }
}