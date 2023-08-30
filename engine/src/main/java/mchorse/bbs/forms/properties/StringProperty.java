package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.serializers.KeyframeSerializers;

public class StringProperty extends BaseProperty<String>
{
    public StringProperty(Form form, String key, String value)
    {
        super(form, key, value);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(data.getString(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.putString(this.getKey(), this.value);
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel()
    {
        return new GenericKeyframeChannel(KeyframeSerializers.STRING);
    }
}