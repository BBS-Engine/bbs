package mchorse.bbs.camera.clips;

import mchorse.bbs.camera.data.Position;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;

public class CameraClipContext extends ClipContext<CameraClip, Position>
{
    @Override
    public boolean apply(Clip clip, Position position)
    {
        if (clip instanceof CameraClip)
        {
            this.currentLayer = clip.layer.get();
            this.relativeTick = this.ticks - clip.tick.get();

            ((CameraClip) clip).apply(this, position);

            this.count += 1;

            return true;
        }

        return false;
    }

    public void shutdown()
    {
        if (this.clips == null)
        {
            return;
        }

        for (Clip clip : this.clips.get())
        {
            if (clip instanceof CameraClip)
            {
                ((CameraClip) clip).shutdown(this);
            }
        }
    }
}