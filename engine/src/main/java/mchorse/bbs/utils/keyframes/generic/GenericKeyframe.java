package mchorse.bbs.utils.keyframes.generic;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;

public class GenericKeyframe <T> implements IMapSerializable
{
    public long tick;
    public T value;
    public IInterpolation interp = Interpolation.LINEAR;

    private final IGenericKeyframeFactory<T> factory;

    public GenericKeyframe(IGenericKeyframeFactory<T> factory)
    {
        this.factory = factory;
    }

    public IGenericKeyframeFactory<T> getFactory()
    {
        return this.factory;
    }

    public void setTick(long tick)
    {
        this.tick = tick;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public void setInterpolation(IInterpolation interp)
    {
        this.interp = interp;
    }

    public GenericKeyframe<T> copy()
    {
        GenericKeyframe<T> frame = new GenericKeyframe<>(this.factory);

        this.copy(frame);

        return frame;
    }

    public void copy(GenericKeyframe<T> keyframe)
    {
        this.tick = keyframe.tick;
        this.value = this.factory.copy(keyframe.value);
        this.interp = keyframe.interp;
    }

    @Override
    public void toData(MapType data)
    {
        data.putLong("tick", this.tick);
        data.put("value", this.factory.toData(this.value));

        if (this.interp != Interpolation.LINEAR) data.putString("interp", this.interp.toString());
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("tick")) this.tick = data.getLong("tick");
        if (data.has("value")) this.value = this.factory.fromData(data.get("value"));
        if (data.has("interp")) this.interp = Interpolation.valueOf(data.getString("interp"));
    }
}