package mchorse.bbs.camera;

import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.camera.values.ValueClips;
import mchorse.bbs.utils.undo.UndoManager;

public class CameraWork extends StructureBase
{
    public ValueClips clips = new ValueClips("clips");

    public UndoManager<CameraWork> undoManager = new UndoManager<CameraWork>(30);

    public CameraWork()
    {
        this.register(this.clips);
    }

    /**
     * Get clip at given layer and clip index.
     */
    public Clip getClip(int i)
    {
        return this.clips.get(i);
    }

    public void apply(ClipContext context, int ticks, float transition, Position position)
    {
        context.clipData.clear();
        context.work = this;
        context.ticks = ticks;
        context.transition = transition;

        for (Clip clip : this.clips.getClips(ticks))
        {
            if (clip instanceof CameraClip)
            {
                context.apply((CameraClip) clip, position);
            }
        }

        context.currentLayer = 0;
    }

    /**
     * Calculate total duration of this camera work.
     */
    public int calculateDuration()
    {
        int max = 0;

        for (Clip clip : this.clips.get())
        {
            max = Math.max(max, clip.tick.get() + clip.duration.get());
        }

        return max;
    }

    public int findNextTick(int tick)
    {
        int output = Integer.MAX_VALUE;

        for (Clip clip : this.clips.get())
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

        for (Clip clip : this.clips.get())
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

    /**
     * Get index of a given clip.
     *
     * @return index of a clip in the thing
     */
    public int getIndex(Clip clip)
    {
        return this.clips.get().indexOf(clip);
    }
}