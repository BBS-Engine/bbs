package mchorse.bbs.utils.keyframes.generic;

import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;

public class GenericKeyframeSegment <T>
{
    public final GenericKeyframe<T> a;
    public final GenericKeyframe<T> b;
    public int duration;
    public float offset;
    public float x;

    public GenericKeyframeSegment(GenericKeyframe<T> a, GenericKeyframe<T> b)
    {
        this.a = a;
        this.b = b;
    }

    public void setup(float ticks)
    {
        int forcedDuration = this.a.getDuration();

        this.duration = forcedDuration > 0 ? forcedDuration : (int) (this.b.getTick() - this.a.getTick());
        this.offset = ticks - this.a.getTick();
        this.x = this.duration == 0 ? 0F : this.offset / (float) this.duration;
    }

    public T createInterpolated()
    {
        IGenericKeyframeFactory<T> factory = this.a.getFactory();

        return factory.copy(factory.interpolate(this.a.getValue(), this.b.getValue(), this.a.getInterpolation(), this.x));
    }

    public boolean isSame()
    {
        return this.a == this.b;
    }

    public GenericKeyframe<T> getClosest()
    {
        return this.x > 0.5F ? this.b : this.a;
    }
}