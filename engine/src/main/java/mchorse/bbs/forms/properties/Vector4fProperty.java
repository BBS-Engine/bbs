package mchorse.bbs.forms.properties;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import org.joml.Vector4f;

public class Vector4fProperty extends BaseTweenProperty<Vector4f>
{
    private Vector4f i = new Vector4f();

    public Vector4fProperty(Form form, String key, Vector4f value)
    {
        super(form, key, value);
    }

    @Override
    protected Vector4f getTweened(float transition)
    {
        float factor = this.interpolation.interpolate(0, 1, this.getTweenFactor(transition));

        this.lastValue.lerp(this.value, factor, this.i);

        return this.i;
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(DataStorageUtils.vector4fFromData(data.getList(key)));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), DataStorageUtils.vector4fToData(this.value));
    }
}