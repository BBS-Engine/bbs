package mchorse.bbs.utils.keyframes.generic;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;

public class GenericKeyframe <T> extends BaseValue
{
    private long tick;
    private T value;
    private IInterpolation interp = Interpolation.LINEAR;

    private final IGenericKeyframeFactory<T> factory;

    public GenericKeyframe(String id, IGenericKeyframeFactory<T> factory, long tick, T value)
    {
        this(id, factory);

        this.tick = tick;
        this.value = value;
    }

    public GenericKeyframe(String id, IGenericKeyframeFactory<T> factory)
    {
        super(id);

        this.factory = factory;
    }

    public IGenericKeyframeFactory<T> getFactory()
    {
        return this.factory;
    }

    public long getTick()
    {
        return this.tick;
    }

    public void setTick(long tick)
    {
        this.preNotifyParent();
        this.tick = tick;
        this.postNotifyParent();
    }

    public T getValue()
    {
        return this.value;
    }

    public void setValue(T value)
    {
        this.preNotifyParent();
        this.value = value;
        this.postNotifyParent();
    }

    public IInterpolation getInterpolation()
    {
        return this.interp;
    }

    public void setInterpolation(IInterpolation interp)
    {
        this.preNotifyParent();
        this.interp = interp;
        this.postNotifyParent();
    }

    public void copy(GenericKeyframe<T> keyframe)
    {
        this.tick = keyframe.tick;
        this.value = this.factory.copy(keyframe.value);
        this.interp = keyframe.interp;
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        data.putLong("tick", this.tick);
        data.put("value", this.factory.toData(this.value));

        if (this.interp != Interpolation.LINEAR) data.putString("interp", this.interp.toString());

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        if (map.has("tick")) this.tick = map.getLong("tick");
        if (map.has("value")) this.value = this.factory.fromData(map.get("value"));
        if (map.has("interp")) this.interp = Interpolation.valueOf(map.getString("interp"));
    }
}