package mchorse.bbs.utils.keyframes.generic;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueList;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

import java.util.List;

/**
 * Keyframe channel
 *
 * This class is responsible for storing individual keyframes and also
 * interpolating between them.
 */
public class GenericKeyframeChannel <T> extends ValueList<GenericKeyframe<T>>
{
    private IGenericKeyframeFactory<T> factory;

    public GenericKeyframeChannel(String id, IGenericKeyframeFactory<T> factory)
    {
        super(id);

        this.factory = factory;
    }

    public IGenericKeyframeFactory<T> getFactory()
    {
        return this.factory;
    }

    /* Read only */

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public List<GenericKeyframe<T>> getKeyframes()
    {
        return this.list;
    }

    public boolean has(int index)
    {
        return index >= 0 && index < this.list.size();
    }

    public GenericKeyframe<T> get(int index)
    {
        return this.has(index) ? this.list.get(index) : null;
    }

    /**
     * Find a keyframe segment at given ticks
     */
    public Pair<GenericKeyframe<T>, GenericKeyframe<T>> findSegment(float ticks)
    {
        /* No keyframes, no values */
        if (this.list.isEmpty())
        {
            return null;
        }

        /* Check whether given ticks are outside keyframe channel's range */
        GenericKeyframe<T> prev = this.list.get(0);

        if (ticks <= prev.getTick())
        {
            return new Pair<>(prev, prev);
        }

        int size = this.list.size();
        GenericKeyframe<T> last = this.list.get(size - 1);

        if (ticks >= last.getTick())
        {
            return new Pair<>(last, last);
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

        GenericKeyframe<T> b = this.list.get(low);

        if (b.getTick() == Math.floor(ticks) && low < size - 1)
        {
            low += 1;
            b = this.list.get(low);
        }

        GenericKeyframe<T> a = low - 1 >= 0 ? this.list.get(low - 1) : b;

        return new Pair<>(a, b);
    }

    /* Write only */

    public void remove(int index)
    {
        if (index < 0 || index > this.list.size() - 1)
        {
            return;
        }

        this.preNotifyParent();
        this.list.remove(index);
        this.postNotifyParent();
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
        this.preNotifyParent();

        GenericKeyframe<T> prev;

        if (!this.list.isEmpty())
        {
            prev = this.list.get(0);

            if (tick < prev.getTick())
            {
                this.list.add(0, new GenericKeyframe<>("", this.factory, tick, value));
                this.sort();

                this.postNotifyParent();

                return 0;
            }
        }

        prev = null;
        int index = 0;

        for (GenericKeyframe<T> frame : this.list)
        {
            if (frame.getTick() == tick)
            {
                frame.setValue(value);
                this.postNotifyParent();

                return index;
            }

            if (prev != null && tick > prev.getTick() && tick < frame.getTick())
            {
                break;
            }

            index++;
            prev = frame;
        }

        GenericKeyframe<T> frame = new GenericKeyframe<>("", this.factory, tick, value);
        this.list.add(index, frame);

        this.sort();
        this.postNotifyParent();

        return index;
    }

    public void sort()
    {
        this.list.sort((a, b) -> (int) (a.getTick() - b.getTick()));

        this.sync();
    }

    @Override
    protected GenericKeyframe<T> create(String id)
    {
        return new GenericKeyframe<>(id, this.factory);
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        data.put("keyframes", super.toData());
        data.putString("type", CollectionUtils.getKey(KeyframeFactories.FACTORIES, this.factory));

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
        IGenericKeyframeFactory<T> factory = KeyframeFactories.FACTORIES.get(map.getString("type"));

        this.factory = factory;

        super.fromData(map.getList("keyframes"));

        this.sort();
    }
}