package mchorse.bbs.utils.keyframes;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Keyframe channel
 *
 * This class is responsible for storing individual keyframes and also
 * interpolating between them.
 */
public class KeyframeChannel implements IDataSerializable<ListType>
{
    private static Pair<Keyframe, Keyframe> segment = new Pair<>();

    protected final List<Keyframe> keyframes = new ArrayList<>();

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
        Pair<Keyframe, Keyframe> segment = this.findSegment(ticks);

        if (segment == null)
        {
            return 0;
        }

        if (segment.a == segment.b)
        {
            return segment.a.value;
        }

        return segment.a.interpolateTicks(segment.b, ticks);
    }

    /**
     * Find a keyframe segment at given ticks
     */
    public Pair<Keyframe, Keyframe> findSegment(float ticks)
    {
        /* No keyframes, no values */
        if (this.keyframes.isEmpty())
        {
            return null;
        }

        /* Check whether given ticks are outside keyframe channel's range */
        Keyframe prev = this.keyframes.get(0);

        if (ticks <= prev.tick)
        {
            segment.set(prev, prev);

            return segment;
        }

        int size = this.keyframes.size();
        Keyframe last = this.keyframes.get(size - 1);

        if (ticks >= last.tick)
        {
            segment.set(last, last);

            return segment;
        }

        /* Use binary search to find the proper segment */
        int low = 0;
        int high = size - 1;

        while (low <= high)
        {
            int mid = low + (high - low) / 2;

            if (this.keyframes.get(mid).tick < ticks)
            {
                low = mid + 1;
            }
            else
            {
                high = mid - 1;
            }
        }

        Keyframe b = this.keyframes.get(low);
        Keyframe a = low - 1 >= 0 ? this.keyframes.get(low - 1) : b;

        segment.set(a, b);

        return segment;
    }

    /**
     * Insert a keyframe at given tick with given value
     *
     * This method is useful as it's not creating keyframes every time you
     * need to add some value, but rather inserts in correct order or
     * overwrites existing keyframe.
     *
     * Also, it returns index at which it was inserted.
     */
    public int insert(long tick, double value)
    {
        Keyframe prev;

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
        this.keyframes.sort((a, b) -> (int) (a.tick - b.tick));

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

    public void simplify()
    {
        if (this.keyframes.size() <= 2)
        {
            return;
        }

        for (int i = 1; i < this.keyframes.size(); i++)
        {
            if (i >= this.keyframes.size() - 1)
            {
                continue;
            }

            Keyframe prev = this.keyframes.get(i - 1);
            Keyframe current = this.keyframes.get(i);
            Keyframe next = this.keyframes.get(i + 1);

            if (current.value == prev.value && current.value == next.value)
            {
                this.keyframes.remove(i);

                i -= 1;
            }
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
            Keyframe keyframe = this.create(0, 0);

            keyframe.fromData(object);
            this.keyframes.add(keyframe);
        }
    }
}
