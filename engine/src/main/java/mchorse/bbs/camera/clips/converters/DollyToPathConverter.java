package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.clips.overwrite.DollyClip;
import mchorse.bbs.camera.clips.overwrite.PathClip;
import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.camera.data.Position;

public class DollyToPathConverter implements IClipConverter<DollyClip, PathClip>
{
    @Override
    public PathClip convert(DollyClip dolly)
    {
        PathClip path = new PathClip();
        Position position = new Position();
        InterpolationType interp = InterpolationType.fromInterp(dolly.interp.get());

        dolly.applyLast(new ClipContext(), position);

        path.copy(dolly);
        path.points.reset();
        path.points.add(dolly.position.get().copy());
        path.points.add(position);
        path.interpolationPoint.set(interp);
        path.interpolationAngle.set(interp);

        return path;
    }
}