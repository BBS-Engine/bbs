package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

public class BooleanProperty extends BaseProperty<Boolean>
{
    public BooleanProperty(Form form, String key, Boolean value)
    {
        super(form, key, value);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getBool(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putBool(this.getKey(), this.value);
    }
}