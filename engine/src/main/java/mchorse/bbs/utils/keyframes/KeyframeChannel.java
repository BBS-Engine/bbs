package mchorse.bbs.utils.keyframes;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keyframe channel
 *
 * This class is responsible for storing individual keyframes and also
 * interpolating between them.
 */
public class KeyframeChannel implements IDataSerializable<ListType>
{
    protected final List<Keyframe> keyframes = new ArrayList<Keyframe>();

    protected Keyframe create(long tick, double value)
    {
        return new Keyframe(tick, value);
    }

    public boolean isEmpty()
    {
        return this.keyframes.isEmpty();
    }

    public List<Keyframe> getKeyframes()
    {
        return this.keyframes;
    }

    public boolean has(int index)
    {
        return index >= 0 && index < this.keyframes.size();
    }

    public Keyframe get(int index)
    {
        return this.has(index) ? this.keyframes.get(index) : null;
    }

    public void remove(int index)
    {
        if (index < 0 || index > this.keyframes.size() - 1)
        {
            return;
        }

        Keyframe frame = this.keyframes.remove(index);

        frame.prev.next = frame.next;
        frame.next.prev = frame.prev;
    }

    /**
     * Calculate the value at given tick
     */
    public double interpolate(float ticks)
    {
        if (this.keyframes.isEmpty())
        {
            return 0;
        }

        Keyframe prev = this.keyframes.get(0);

        if (ticks < prev.tick)
        {
            return prev.value;
        }

        prev = null;

        for (Keyframe frame : this.keyframes)
        {
            if (prev != null && ticks >= prev.tick && ticks < frame.tick)
            {
                return prev.interpolate(frame, (ticks - prev.tick) / (frame.tick - prev.tick));
            }

            prev = frame;
        }

        return prev.value;
    }

    /**
     * Insert a keyframe at given tick with given value
     *
     * This method is useful as it's not creating keyframes every time you
     * need to add some value, but rather inserts in correct order or
     * overwrites existing keyframe.
     *
     * Also it returns index at which it was inserted.
     */
    public int insert(long tick, double value)
    {
        Keyframe prev = null;

        if (!this.keyframes.isEmpty())
        {
            prev = this.keyframes.get(0);

            if (tick < prev.tick)
            {
                this.keyframes.add(0, this.create(tick, value));

                return 0;
            }
        }

        prev = null;
        int index = 0;

        for (Keyframe frame : this.keyframes)
        {
            if (frame.tick == tick)
            {
                frame.value = value;

                return index;
            }

            if (prev != null && tick > prev.tick && tick < frame.tick)
            {
                break;
            }

            index++;
            prev = frame;
        }

        Keyframe frame = this.create(tick, value);
        this.keyframes.add(index, frame);

        if (this.keyframes.size() > 1)
        {
            frame.prev = this.keyframes.get(Math.max(index - 1, 0));
            frame.next = this.keyframes.get(Math.min(index + 1, this.keyframes.size() - 1));
        }

        return index;
    }

    public void moveX(long offset)
    {
        for (Keyframe keyframe : this.keyframes)
        {
            keyframe.tick += offset;
        }
    }

    /**
     * Sorts keyframes based on their ticks. This method should be used
     * when you modify individual tick values of keyframes.
     * {@link #interpolate(float)} and other methods assume the order of
     * the keyframes to be chronologically correct.
     */
    public void sort()
    {
        Collections.sort(this.keyframes, (a, b) -> (int) (a.tick - b.tick));

        if (!this.keyframes.isEmpty())
        {
            Keyframe prev = this.keyframes.get(0);

            for (Keyframe frame : this.keyframes)
            {
                frame.prev = prev;
                prev.next = frame;

                prev = frame;
            }

            prev.next = prev;
        }
    }

    public void copy(KeyframeChannel channel)
    {
        this.keyframes.clear();

        for (Keyframe frame : channel.keyframes)
        {
            this.keyframes.add(frame.copy());
        }

        this.sort();
    }

    @Override
    public ListType toData()
    {
        ListType list = new ListType();

        for (Keyframe keyframe : this.keyframes)
        {
            list.add(keyframe.toData());
        }

        return list;
    }

    @Override
    public void fromData(ListType data)
    {
        this.keyframes.clear();

        for (BaseType element : data)
        {
            if (!element.isMap())
            {
                continue;
            }

            MapType object = element.asMap();
            Keyframe keyframe = new Keyframe();

            keyframe.fromData(object);
            this.keyframes.add(keyframe);
        }
    }
}
