package mchorse.bbs.film.replays;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class FormProperty extends BaseValueBasic<GenericKeyframeChannel>
{
    public FormProperty(String id)
    {
        super(id, null);
    }

    @Override
    public BaseType toData()
    {
        MapType type = new MapType();

        if (this.value != null)
        {
            type.putString("type", CollectionUtils.getKey(KeyframeFactories.SERIALIZERS, this.value.getFactory()));
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

        if (this.value == null)
        {
            IGenericKeyframeFactory serializer = KeyframeFactories.SERIALIZERS.get(map.getString("type"));
            GenericKeyframeChannel channel = new GenericKeyframeChannel(serializer);

            channel.fromData(map.getList("keyframes"));
            this.value = channel;
        }
        else
        {
            this.value.fromData(map.getList("keyframes"));
        }
    }
}