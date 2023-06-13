package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.overwrite.IdleClip;
import mchorse.bbs.camera.clips.overwrite.PathClip;

public class IdleToPathConverter implements IClipConverter<IdleClip, PathClip>
{
    @Override
    public PathClip convert(IdleClip clip)
    {
        PathClip pathClip = new PathClip();

        pathClip.copy(clip);
        pathClip.points.get().clear();
        pathClip.points.add(clip.position.get().copy());

        return pathClip;
    }
}