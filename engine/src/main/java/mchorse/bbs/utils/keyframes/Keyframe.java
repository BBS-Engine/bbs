package mchorse.bbs.utils.keyframes;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValue;

public class Keyframe extends BaseValue
{
    public Keyframe prev;
    public Keyframe next;

    private long tick;
    private double value;

    private KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;
    private KeyframeEasing easing = KeyframeEasing.IN;

    private float rx = 5;
    private float ry;
    private float lx = 5;
    private float ly;

    public Keyframe(String id, long tick, double value)
    {
        this(id);

        this.tick = tick;
        this.value = value;
    }

    public Keyframe(String id)
    {
        super(id);

        this.prev = this;
        this.next = this;
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

    public double getValue()
    {
        return this.value;
    }

    public void setValue(double value)
    {
        this.preNotifyParent();

        this.value = value;

        this.postNotifyParent();
    }

    public KeyframeInterpolation getInterpolation()
    {
        return this.interp;
    }

    public void setInterpolation(KeyframeInterpolation interp)
    {
        this.preNotifyParent();

        this.interp = interp;

        this.postNotifyParent();
    }

    public void setInterpolation(KeyframeInterpolation interp, KeyframeEasing easing)
    {
        this.preNotifyParent();

        this.interp = interp;
        this.easing = easing;

        this.postNotifyParent();
    }

    public KeyframeEasing getEasing()
    {
        return this.easing;
    }

    public void setEasing(KeyframeEasing easing)
    {
        this.preNotifyParent();

        this.easing = easing;

        this.postNotifyParent();
    }

    public float getRx()
    {
        return this.rx;
    }

    public void setRx(float rx)
    {
        this.preNotifyParent();

        this.rx = rx;

        this.postNotifyParent();
    }

    public float getRy()
    {
        return this.ry;
    }

    public void setRy(float ry)
    {
        this.preNotifyParent();

        this.ry = ry;

        this.postNotifyParent();
    }

    public float getLx()
    {
        return this.lx;
    }

    public void setLx(float lx)
    {
        this.preNotifyParent();

        this.lx = lx;

        this.postNotifyParent();
    }

    public float getLy()
    {
        return this.ly;
    }

    public void setLy(float ly)
    {
        this.preNotifyParent();

        this.ly = ly;

        this.postNotifyParent();
    }

    public double interpolateTicks(Keyframe frame, double ticks)
    {
        return this.interp.interpolate(this, frame, (ticks - this.tick) / (frame.tick - this.tick));
    }

    public double interpolate(Keyframe frame, double x)
    {
        return this.interp.interpolate(this, frame, x);
    }

    public Keyframe copy()
    {
        Keyframe frame = new Keyframe("", this.tick, this.value);

        frame.copy(this);

        return frame;
    }

    public void copy(Keyframe keyframe)
    {
        this.tick = keyframe.tick;
        this.value = keyframe.value;
        this.interp = keyframe.interp;
        this.easing = keyframe.easing;
        this.lx = keyframe.lx;
        this.ly = keyframe.ly;
        this.rx = keyframe.rx;
        this.ry = keyframe.ry;
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        data.putLong("tick", this.tick);
        data.putDouble("value", this.value);

        if (this.interp != KeyframeInterpolation.LINEAR) data.putInt("interp", this.interp.ordinal());
        if (this.easing != KeyframeEasing.IN) data.putInt("easing", this.easing.ordinal());
        if (this.rx != 5) data.putFloat("rx", this.rx);
        if (this.ry != 0) data.putFloat("ry", this.ry);
        if (this.lx != 5) data.putFloat("lx", this.lx);
        if (this.ly != 0) data.putFloat("ly", this.ly);

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
        if (map.has("value")) this.value = map.getDouble("value");
        if (map.has("interp")) this.interp = KeyframeInterpolation.values()[map.getInt("interp")];
        if (map.has("easing")) this.easing = KeyframeEasing.values()[map.getInt("easing")];
        if (map.has("rx")) this.rx = map.getFloat("rx");
        if (map.has("ry")) this.ry = map.getFloat("ry");
        if (map.has("lx")) this.lx = map.getFloat("lx");
        if (map.has("ly")) this.ly = map.getFloat("ly");
    }
}
