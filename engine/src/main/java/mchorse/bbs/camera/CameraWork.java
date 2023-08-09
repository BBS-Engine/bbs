package mchorse.bbs.camera;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.values.ValueClips;

public class CameraWork extends StructureBase
{
    public ValueClips clips = new ValueClips("clips", BBS.getFactoryClips());

    public CameraWork()
    {
        this.register(this.clips);
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