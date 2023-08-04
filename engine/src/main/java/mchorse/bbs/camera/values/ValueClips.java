package mchorse.bbs.camera.values;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.settings.values.ValueGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueClips extends ValueGroup
{
    private List<Clip> clips;

    public ValueClips(String id)
    {
        super(id);

        this.assign(new ArrayList<Clip>());
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
        Map<Integer, Clip> clipMap = new HashMap<Integer, Clip>();

        for (Clip clip : this.clips)
        {
            boolean isGlobal = clip.isGlobal() && maxLayer == Integer.MAX_VALUE;

            if ((clip.isInside(tick) || isGlobal) && clip.layer.get() < maxLayer)
            {
                clipMap.put(clip.layer.get(), clip);
            }
        }

        List<Clip> clips = new ArrayList<Clip>(clipMap.values());

        clips.sort(Comparator.comparingInt((a) -> a.layer.get()));

        return clips;
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
            this.add(new ValueClip(String.valueOf(i), this.clips.get(i)));
        }
    }

    public List<Clip> get()
    {
        return Collections.unmodifiableList(this.clips);
    }

    /* Value implementation */

    @Override
    public void reset()
    {
        this.assign(new ArrayList<Clip>());
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (Clip clip : this.clips)
        {
            list.add(BBS.getFactoryClips().toData(clip));
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

            Clip clip = BBS.getFactoryClips().fromData(type.asMap());

            if (clip != null)
            {
                this.clips.add(clip);
            }
        }

        this.sync();
    }
}