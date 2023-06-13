package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UISheet
{
    public final String id;
    public IKey title;
    public int color;
    public KeyframeChannel channel;
    public List<Integer> selected = new ArrayList<Integer>();
    public boolean handles = true;

    public UISheet(String id, IKey title, int color, KeyframeChannel channel, boolean handles)
    {
        this(id, title, color, channel);

        this.handles = handles;
    }

    public UISheet(String id, IKey title, int color, KeyframeChannel channel)
    {
        this.id = id;
        this.title = title;
        this.color = color;
        this.channel = channel;
    }

    public void sort()
    {
        List<Keyframe> keyframes = new ArrayList<Keyframe>();

        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframes.add(keyframe);
            }
        }

        this.channel.sort();
        this.selected.clear();

        for (Keyframe keyframe : keyframes)
        {
            this.selected.add(this.channel.getKeyframes().indexOf(keyframe));
        }
    }

    public void setTick(double dx, Selection selection, boolean opposite)
    {
        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                selection.setX(keyframe, selection.getX(keyframe) + dx, opposite);
            }
        }
    }

    public void setValue(double dy, Selection selection, boolean opposite)
    {
        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                selection.setY(keyframe, selection.getY(keyframe) + dy, opposite);
            }
        }
    }

    public void setInterpolation(KeyframeInterpolation interp)
    {
        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframe.setInterpolation(interp);
            }
        }
    }

    public void setEasing(KeyframeEasing easing)
    {
        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                keyframe.setEasing(easing);
            }
        }
    }

    public Keyframe getKeyframe()
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
        List<Integer> sorted = new ArrayList<Integer>(this.selected);

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
        List<Keyframe> selected = new ArrayList<Keyframe>();
        List<Keyframe> created = new ArrayList<Keyframe>();

        long minTick = Integer.MAX_VALUE;

        for (int index : this.selected)
        {
            Keyframe keyframe = this.channel.get(index);

            if (keyframe != null)
            {
                selected.add(keyframe);
                minTick = Math.min(keyframe.tick, minTick);
            }
        }

        selected.sort(Comparator.comparingLong(a -> a.tick));

        long diff = tick - minTick;

        for (Keyframe keyframe : selected)
        {
            long fin = keyframe.tick + diff;
            int index = this.channel.insert(fin, keyframe.value);
            Keyframe current = this.channel.get(index);

            current.copy(keyframe);
            current.tick = fin;
            created.add(current);
        }

        this.clearSelection();

        for (Keyframe keyframe : created)
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
