package mchorse.bbs.film.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.serializers.IGenericKeyframeSerializer;
import mchorse.bbs.utils.keyframes.generic.serializers.KeyframeSerializers;

public class ValueFormProperty extends BaseValueBasic<GenericKeyframeChannel>
{
    public ValueFormProperty(String id)
    {
        super(id);
    }

    @Override
    public BaseType toData()
    {
        MapType type = new MapType();

        if (this.value != null)
        {
            type.putString("type", CollectionUtils.getKey(KeyframeSerializers.SERIALIZERS, this.value.getSerializer()));
            type.put("keyframes", this.value.toData());
        }

        return type;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();
        IGenericKeyframeSerializer serializer = KeyframeSerializers.SERIALIZERS.get(map.getString("type"));
        GenericKeyframeChannel channel = new GenericKeyframeChannel(serializer);

        channel.fromData(map.getList("keyframes"));
        this.set(channel);
    }
}