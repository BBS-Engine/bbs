package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.clips.overwrite.IdleClip;

public class IdleConverter
{
    public static final IClipConverter CONVERTER = (clip) ->
    {
        IdleClip idle = new IdleClip();

        idle.copy(clip);

        if (clip instanceof CameraClip)
        {
            ((CameraClip) clip).apply(new ClipContext().setup(clip.tick.get(), 0, 0), idle.position.get());
        }

        return idle;
    };
}