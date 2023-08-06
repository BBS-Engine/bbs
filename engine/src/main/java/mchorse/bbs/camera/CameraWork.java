package mchorse.bbs.camera;

import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.camera.values.ValueClips;

public class CameraWork extends StructureBase
{
    public ValueClips clips = new ValueClips("clips");

    public CameraWork()
    {
        this.register(this.clips);
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
}