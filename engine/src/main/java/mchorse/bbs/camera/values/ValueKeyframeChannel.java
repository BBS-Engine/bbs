package mchorse.bbs.camera.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

public class ValueKeyframeChannel extends BaseValue
{
    private KeyframeChannel channel;

    public ValueKeyframeChannel(String id)
    {
        this(id, new KeyframeChannel());
    }

    public ValueKeyframeChannel(String id, KeyframeChannel channel)
    {
        super(id);

        this.channel = channel;
    }

    public KeyframeChannel get()
    {
        return this.channel;
    }

    public void set(KeyframeChannel channel)
    {
        this.channel.copy(channel);
    }

    @Override
    public BaseType toData()
    {
        return this.channel.toData();
    }

    @Override
    public void fromData(BaseType data)
    {
        this.channel.fromData(data.asList());
        this.channel.sort();
    }
}