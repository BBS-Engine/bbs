package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.utils.clips.Clip;

public interface IClipConverter <A extends Clip, B extends Clip>
{
    public B convert(A clip);
}