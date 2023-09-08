package mchorse.bbs.utils.keyframes;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.ValueList;
import mchorse.bbs.utils.Pair;

import java.util.List;

/**
 * Keyframe channel
 *
 * <p>This class is responsible for storing individual keyframes and also
 * interpolating between them.</p>
 */
public class KeyframeChannel extends ValueList<Keyframe>
{
    private static final Pair<Keyframe, Keyframe> segment = new Pair<>();

    public KeyframeChannel()
    {
        super("");
    }

    public KeyframeChannel(String id)
    {
        super(id);
    }

    public int getLength()
    {
        return this.list.isEmpty() ? 0 : (int) this.list.get(this.list.size() - 1).getTick();
    }

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public List<Keyframe> getKeyframes()
    {
        return this.list;
    }

    public boolean has(int index)
    {
        return index >= 0 && index < this.list.size();
    }

    public Keyframe get(int index)
    {
        return this.has(index) ? this.list.get(index) : null;
    }

    public void remove(int index)
    {
        if (index < 0 || index > this.list.size() - 1)
        {
            return;
        }

        Keyframe frame = this.list.remove(index);

        frame.prev.next = frame.next;
        frame.next.prev = frame.prev;

        this.sync();
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
            return segment.a.getValue();
        }

        return segment.a.interpolateTicks(segment.b, ticks);
    }

    /**
     * Find a keyframe segment at given ticks
     */
    public Pair<Keyframe, Keyframe> findSegment(float ticks)
    {
        /* No keyframes, no values */
        if (this.list.isEmpty())
        {
            return null;
        }

        /* Check whether given ticks are outside keyframe channel's range */
        Keyframe prev = this.list.get(0);

        if (ticks <= prev.getTick())
        {
            segment.set(prev, prev);

            return segment;
        }

        int size = this.list.size();
        Keyframe last = this.list.get(size - 1);

        if (ticks >= last.getTick())
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

            if (this.list.get(mid).getTick() < ticks)
            {
                low = mid + 1;
            }
            else
            {
                high = mid - 1;
            }
        }

        Keyframe b = this.list.get(low);
        Keyframe a = low - 1 >= 0 ? this.list.get(low - 1) : b;

        segment.set(a, b);

        return segment;
    }

    /**
     * Insert a keyframe at given tick with given value
     *
     * <p>This method is useful as it's not creating keyframes every time you
     * need to add some value, but rather inserts in correct order or
     * overwrites existing keyframe.</p>
     *
     * <p>Also, it returns index at which it was inserted.</p>
     */
    public int insert(long tick, double value)
    {
        Keyframe prev;

        if (!this.list.isEmpty())
        {
            prev = this.list.get(0);

            if (tick < prev.getTick())
            {
                this.list.add(0, new Keyframe("", tick, value));

                this.sort();

                return 0;
            }
        }

        prev = null;
        int index = 0;

        for (Keyframe frame : this.list)
        {
            if (frame.getTick() == tick)
            {
                frame.setValue(value);

                return index;
            }

            if (prev != null && tick > prev.getTick() && tick < frame.getTick())
            {
                break;
            }

            index++;
            prev = frame;
        }

        Keyframe frame = new Keyframe("", tick, value);
        this.list.add(index, frame);

        if (this.list.size() > 1)
        {
            frame.prev = this.list.get(Math.max(index - 1, 0));
            frame.next = this.list.get(Math.min(index + 1, this.list.size() - 1));
        }

        this.sync();

        return index;
    }

    public void moveX(long offset)
    {
        for (Keyframe keyframe : this.list)
        {
            keyframe.setTick(keyframe.getTick() + offset);
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
        this.list.sort((a, b) -> (int) (a.getTick() - b.getTick()));

        if (!this.list.isEmpty())
        {
            Keyframe prev = this.list.get(0);

            for (Keyframe frame : this.list)
            {
                frame.prev = prev;
                prev.next = frame;

                prev = frame;
            }

            prev.next = prev;
        }

        this.sync();
    }

    public void simplify()
    {
        if (this.list.size() <= 2)
        {
            return;
        }

        for (int i = 1; i < this.list.size(); i++)
        {
            if (i >= this.list.size() - 1)
            {
                continue;
            }

            Keyframe prev = this.list.get(i - 1);
            Keyframe current = this.list.get(i);
            Keyframe next = this.list.get(i + 1);

            if (current.getValue() == prev.getValue() && current.getValue() == next.getValue())
            {
                this.list.remove(i);

                i -= 1;
            }
        }

        this.sync();
    }

    @Override
    protected Keyframe create(String id)
    {
        return new Keyframe(id);
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        this.sort();
    }
}