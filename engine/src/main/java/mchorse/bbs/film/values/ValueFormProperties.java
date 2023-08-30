package mchorse.bbs.film.values;

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

public class ValueFormProperties extends ValueGroup
{
    public final Map<String, ValueFormProperty> properties = new HashMap<>();

    public ValueFormProperties(String id)
    {
        super(id);
    }

    public ValueFormProperty getOrCreate(Form form, String key)
    {
        BaseValue value = this.get(key);

        if (value instanceof ValueFormProperty)
        {
            return (ValueFormProperty) value;
        }

        IFormProperty property = FormUtils.getProperty(form, key);

        return property != null ? this.create(property) : null;
    }

    public ValueFormProperty create(IFormProperty property)
    {
        if (property.canCreateChannel())
        {
            GenericKeyframeChannel channel = property.createChannel();
            String key = FormUtils.getPropertyPath(property);
            ValueFormProperty valueFormProperty = new ValueFormProperty(key);

            valueFormProperty.set(channel);
            this.properties.put(key, valueFormProperty);
            this.add(valueFormProperty);

            return valueFormProperty;
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

            ValueFormProperty property = new ValueFormProperty(key);

            property.fromData(mapType);

            if (property.get().getFactory() != null)
            {
                this.properties.put(key, property);
                this.add(property);
            }
        }
    }
}