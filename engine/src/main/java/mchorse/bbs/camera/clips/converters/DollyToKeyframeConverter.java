package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.overwrite.DollyClip;
import mchorse.bbs.camera.clips.overwrite.KeyframeClip;

public class DollyToKeyframeConverter implements IClipConverter<DollyClip, KeyframeClip>
{
    @Override
    public KeyframeClip convert(DollyClip clip)
    {
        return new PathToKeyframeConverter().convert(new DollyToPathConverter().convert(clip));
    }
}