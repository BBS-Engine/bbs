package mchorse.bbs.film.replays;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;

import java.util.HashMap;
import java.util.Map;

public class FormProperties extends ValueGroup
{
    public final Map<String, GenericKeyframeChannel> properties = new HashMap<>();

    public FormProperties(String id)
    {
        super(id);
    }

    public GenericKeyframeChannel getOrCreate(Form form, String key)
    {
        BaseValue value = this.get(key);

        if (value instanceof GenericKeyframeChannel)
        {
            return (GenericKeyframeChannel) value;
        }

        IFormProperty property = FormUtils.getProperty(form, key);

        return property != null ? this.create(property) : null;
    }

    public GenericKeyframeChannel create(IFormProperty property)
    {
        if (property.canCreateChannel())
        {
            String key = FormUtils.getPropertyPath(property);
            GenericKeyframeChannel channel = property.createChannel(key);

            this.properties.put(key, channel);
            this.add(channel);

            return channel;
        }

        return null;
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        this.properties.clear();

        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (String key : map.keys())
        {
            MapType mapType = map.getMap(key);

            if (mapType.isEmpty())
            {
                continue;
            }

            GenericKeyframeChannel property = new GenericKeyframeChannel(key, null);

            property.fromData(mapType);

            if (property.getFactory() != null)
            {
                this.properties.put(key, property);
                this.add(property);
            }
        }
    }
}