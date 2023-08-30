package mchorse.bbs.utils.keyframes.generic;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Keyframe channel
 *
 * This class is responsible for storing individual keyframes and also
 * interpolating between them.
 */
public class GenericKeyframeChannel <T> implements IDataSerializable<ListType>
{
    private final List<GenericKeyframe<T>> keyframes = new ArrayList<>();
    private final IGenericKeyframeFactory<T> factory;

    public GenericKeyframeChannel(IGenericKeyframeFactory<T> factory)
    {
        this.factory = factory;
    }

    public IGenericKeyframeFactory<T> getFactory()
    {
        return this.factory;
    }

    protected GenericKeyframe<T> create(long tick, T value)
    {
        GenericKeyframe<T> keyframe = new GenericKeyframe<>(this.factory);

        keyframe.tick = tick;
        keyframe.value = value;

        return keyframe;
    }

    public boolean isEmpty()
    {
        return this.keyframes.isEmpty();
    }

    public List<GenericKeyframe<T>> getKeyframes()
    {
        return this.keyframes;
    }

    public boolean has(int index)
    {
        return index >= 0 && index < this.keyframes.size();
    }

    public GenericKeyframe<T> get(int index)
    {
        return this.has(index) ? this.keyframes.get(index) : null;
    }

    public void remove(int index)
    {
        if (index < 0 || index > this.keyframes.size() - 1)
        {
            return;
        }

        this.keyframes.remove(index);
    }

    /**
     * Find a keyframe segment at given ticks
     */
    public Pair<GenericKeyframe<T>, GenericKeyframe<T>> findSegment(float ticks)
    {
        /* No keyframes, no values */
        if (this.keyframes.isEmpty())
        {
            return null;
        }

        /* Check whether given ticks are outside keyframe channel's range */
        GenericKeyframe<T> prev = this.keyframes.get(0);

        if (ticks <= prev.tick)
        {
            return new Pair<>(prev, prev);
        }

        int size = this.keyframes.size();
        GenericKeyframe<T> last = this.keyframes.get(size - 1);

        if (ticks >= last.tick)
        {
            return new Pair<>(last, last);
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

        GenericKeyframe<T> b = this.keyframes.get(low);
        GenericKeyframe<T> a = low - 1 >= 0 ? this.keyframes.get(low - 1) : b;

        return new Pair<>(a, b);
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
    public int insert(long tick, T value)
    {
        GenericKeyframe<T> prev;

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

        for (GenericKeyframe<T> frame : this.keyframes)
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

        GenericKeyframe<T> frame = this.create(tick, value);
        this.keyframes.add(index, frame);

        return index;
    }

    public void moveX(long offset)
    {
        for (GenericKeyframe<T> keyframe : this.keyframes)
        {
            keyframe.tick += offset;
        }
    }

    public void sort()
    {
        this.keyframes.sort((a, b) -> (int) (a.tick - b.tick));
    }

    public void copy(GenericKeyframeChannel<T> channel)
    {
        this.keyframes.clear();

        for (GenericKeyframe<T> frame : channel.keyframes)
        {
            this.keyframes.add(frame.copy());
        }

        this.sort();
    }

    @Override
    public ListType toData()
    {
        ListType list = new ListType();

        for (GenericKeyframe<T> keyframe : this.keyframes)
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
            GenericKeyframe<T> keyframe = this.create(0, null);

            keyframe.fromData(object);
            this.keyframes.add(keyframe);
        }
    }
}