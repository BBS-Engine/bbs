package mchorse.bbs.ui.film.replays.properties;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.math.IInterpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UIProperty
{
    public final String id;
    public IKey title;
    public int color;
    public GenericKeyframeChannel channel;
    public List<Integer> selected = new ArrayList<>();

    public UIProperty(String id, IKey title, int color, GenericKeyframeChannel channel)
    {
        this.id = id;
        this.title = title;
        this.color = color;
        this.channel = channel;
    }

    public void sort()
    {
        List<GenericKeyframe> keyframes = new ArrayList<>();

        for (int index : this.selected)
        {
            GenericKeyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframes.add(keyframe);
            }
        }

        this.channel.sort();
        this.selected.clear();

        for (GenericKeyframe keyframe : keyframes)
        {
            this.selected.add(this.channel.getKeyframes().indexOf(keyframe));
        }
    }

    public void setTick(double dx)
    {
        for (int index : this.selected)
        {
            GenericKeyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframe.tick += dx;
            }
        }
    }

    public void setValue(Object object)
    {
        for (int index : this.selected)
        {
            GenericKeyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframe.value = this.channel.getFactory().copy(object);
            }
        }
    }

    public void setInterpolation(IInterpolation interp)
    {
        for (int index : this.selected)
        {
            GenericKeyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframe.setInterpolation(interp);
            }
        }
    }

    public GenericKeyframe getKeyframe()
    {
        if (this.selected.isEmpty())
        {
            return null;
        }

        return this.channel.get(this.selected.get(0));
    }

    public boolean hasSelected(int i)
    {
        return this.selected.contains(i);
    }

    public void clearSelection()
    {
        this.selected.clear();
    }

    public int getSelectedCount()
    {
        return this.selected.size();
    }

    public void removeSelectedKeyframes()
    {
        List<Integer> sorted = new ArrayList<>(this.selected);

        Collections.sort(sorted);
        Collections.reverse(sorted);

        this.clearSelection();

        for (int index : sorted)
        {
            this.channel.remove(index);
        }

        this.clearSelection();
    }

    public void duplicate(long tick)
    {
        List<GenericKeyframe> selected = new ArrayList<>();
        List<GenericKeyframe> created = new ArrayList<>();

        long minTick = Integer.MAX_VALUE;

        for (int index : this.selected)
        {
            GenericKeyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                selected.add(keyframe);
                minTick = Math.min(keyframe.tick, minTick);
            }
        }

        selected.sort(Comparator.comparingLong(a -> a.tick));

        long diff = tick - minTick;

        for (GenericKeyframe keyframe : selected)
        {
            long fin = keyframe.tick + diff;
            int index = this.channel.insert(fin, keyframe.value);
            GenericKeyframe current = this.channel.get(index);

            current.copy(keyframe);
            current.tick = fin;
            created.add(current);
        }

        this.clearSelection();

        for (GenericKeyframe keyframe : created)
        {
            this.selected.add(this.channel.getKeyframes().indexOf(keyframe));
        }
    }

    public void selectAll()
    {
        this.clearSelection();

        for (int i = 0, c = this.channel.getKeyframes().size(); i < c; i++)
        {
            this.selected.add(i);
        }
    }
}
