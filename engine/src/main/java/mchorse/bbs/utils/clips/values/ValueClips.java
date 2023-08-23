package mchorse.bbs.utils.clips.values;

import mchorse.bbs.camera.clips.ClipFactoryData;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.factory.IFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueClips extends ValueGroup
{
    private static Map<Integer, Clip> clipMap = new HashMap<>();

    private List<Clip> clips;
    private IFactory<Clip, ClipFactoryData> factory;

    public ValueClips(String id, IFactory<Clip, ClipFactoryData> factory)
    {
        super(id);

        this.factory = factory;

        this.assign(new ArrayList<>());
    }

    /**
     * Calculate total duration of this camera work.
     */
    public int calculateDuration()
    {
        int max = 0;

        for (Clip clip : this.clips)
        {
            max = Math.max(max, clip.tick.get() + clip.duration.get());
        }

        return max;
    }

    public Clip get(int index)
    {
        return index >= 0 && index < this.clips.size() ? this.clips.get(index) : null;
    }

    public Clip getClipAt(int tick, int layer)
    {
        for (Clip clip : this.clips)
        {
            if (clip.isInside(tick) && clip.layer.get() == layer)
            {
                return clip;
            }
        }

        return null;
    }

    public List<Clip> getClips(int tick)
    {
        return this.getClips(tick, Integer.MAX_VALUE);
    }

    public List<Clip> getClips(int tick, int maxLayer)
    {
        clipMap.clear();

        for (Clip clip : this.clips)
        {
            boolean isGlobal = clip.isGlobal() && maxLayer == Integer.MAX_VALUE;

            if ((clip.isInside(tick) || isGlobal) && clip.layer.get() < maxLayer)
            {
                clipMap.put(clip.layer.get(), clip);
            }
        }

        List<Clip> clipList = new ArrayList<>();

        clipList.clear();
        clipList.addAll(clipMap.values());
        clipList.sort(Comparator.comparingInt((a) -> a.layer.get()));

        return clipList;
    }

    /**
     * Get index of a given clip.
     *
     * @return index of a clip in the thing
     */
    public int getIndex(Clip clip)
    {
        return this.clips.indexOf(clip);
    }

    public void add(Clip clip)
    {
        this.clips.add(clip);

        this.sync();
    }

    public void remove(int index)
    {
        this.clips.remove(index);

        this.sync();
    }

    public void remove(Clip clip)
    {
        this.clips.remove(clip);

        this.sync();
    }

    /* New value methods */

    public void assign(List<Clip> clips)
    {
        this.clips = clips;

        this.sync();
    }

    public void sync()
    {
        this.removeAll();

        for (int i = 0, c = this.clips.size(); i < c; i++)
        {
            this.add(new ValueClip(String.valueOf(i), this.clips.get(i), this.factory));
        }
    }

    public List<Clip> get()
    {
        return Collections.unmodifiableList(this.clips);
    }

    public int findNextTick(int tick)
    {
        int output = Integer.MAX_VALUE;

        for (Clip clip : this.clips)
        {
            int left = clip.tick.get() - tick;
            int right = left + clip.duration.get();

            int a = Math.max(left, 0);
            int b = Math.max(right, 0);

            if (a > 0)
            {
                output = Math.min(output, a);
            }
            else if (b > 0)
            {
                output = Math.min(output, b);
            }
        }

        return tick + (output != Integer.MAX_VALUE ? output : 0);
    }

    public int findPreviousTick(int tick)
    {
        int output = Integer.MIN_VALUE;

        for (Clip clip : this.clips)
        {
            int left = clip.tick.get() - tick;
            int right = left + clip.duration.get();

            int a = Math.min(left, -0);
            int b = Math.min(right, -0);

            if (b < -0)
            {
                output = Math.max(output, b);
            }
            else if (a < -0)
            {
                output = Math.max(output, a);
            }
        }

        return tick + (output != Integer.MIN_VALUE ? output : 0);
    }

    /* Value implementation */

    @Override
    public void reset()
    {
        this.assign(new ArrayList<>());
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (Clip clip : this.clips)
        {
            list.add(this.factory.toData(clip));
        }

        return list;
    }

    @Override
    public void fromData(BaseType base)
    {
        this.clips.clear();

        for (BaseType type : base.asList())
        {
            if (!type.isMap())
            {
                continue;
            }

            Clip clip = this.factory.fromData(type.asMap());

            if (clip != null)
            {
                this.clips.add(clip);
            }
        }

        this.sync();
    }
}