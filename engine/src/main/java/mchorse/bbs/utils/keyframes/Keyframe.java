package mchorse.bbs.utils.keyframes;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class Keyframe implements IMapSerializable
{
    public Keyframe prev;
    public Keyframe next;

    public long tick;
    public double value;

    public KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;
    public KeyframeEasing easing = KeyframeEasing.IN;

    public float rx = 5;
    public float ry;
    public float lx = 5;
    public float ly;

    public Keyframe(long tick, double value)
    {
        this();

        this.tick = tick;
        this.value = value;
    }

    public Keyframe()
    {
        this.prev = this;
        this.next = this;
    }

    public void setTick(long tick)
    {
        this.tick = tick;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public void setInterpolation(KeyframeInterpolation interp)
    {
        this.interp = interp;
    }

    public void setInterpolation(KeyframeInterpolation interp, KeyframeEasing easing)
    {
        this.interp = interp;
        this.setEasing(easing);
    }

    public void setEasing(KeyframeEasing easing)
    {
        this.easing = easing;
    }

    public double interpolate(Keyframe frame, double x)
    {
        return this.interp.interpolate(this, frame, x);
    }

    public Keyframe copy()
    {
        Keyframe frame = new Keyframe(this.tick, this.value);

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
    public void toData(MapType data)
    {
        data.putLong("tick", this.tick);
        data.putDouble("value", this.value);

        if (this.interp != KeyframeInterpolation.LINEAR) data.putInt("interp", this.interp.ordinal());
        if (this.easing != KeyframeEasing.IN) data.putInt("easing", this.easing.ordinal());
        if (this.rx != 5) data.putFloat("rx", this.rx);
        if (this.ry != 0) data.putFloat("ry", this.ry);
        if (this.lx != 5) data.putFloat("lx", this.lx);
        if (this.ly != 0) data.putFloat("ly", this.ly);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("tick")) this.tick = data.getLong("tick");
        if (data.has("value")) this.value = data.getDouble("value");
        if (data.has("interp")) this.interp = KeyframeInterpolation.values()[data.getInt("interp")];
        if (data.has("easing")) this.easing = KeyframeEasing.values()[data.getInt("easing")];
        if (data.has("rx")) this.rx = data.getFloat("rx");
        if (data.has("ry")) this.ry = data.getFloat("ry");
        if (data.has("lx")) this.lx = data.getFloat("lx");
        if (data.has("ly")) this.ly = data.getFloat("ly");
    }
}
