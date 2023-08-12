package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.Transform;

public class TransformProperty extends BaseTweenProperty<Transform>
{
    private Transform i = new Transform();

    public TransformProperty(Form form, String key, Transform value)
    {
        super(form, key, value);
    }

    @Override
    protected Transform getTweened(float transition)
    {
        float factor = this.interpolation.interpolate(0, 1, this.getTweenFactor(transition));

        this.i.copy(this.lastValue);
        this.i.lerp(this.value, factor);

        return this.i;
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.value.fromData(data.getMap(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), this.value.toData());
    }
}