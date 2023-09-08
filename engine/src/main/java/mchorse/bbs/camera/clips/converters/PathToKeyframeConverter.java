package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.camera.clips.overwrite.PathClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

public class PathToKeyframeConverter implements IClipConverter<PathClip, KeyframeClip>
{
    @Override
    public KeyframeClip convert(PathClip path)
    {
        int c = path.size();

        if (c <= 1)
        {
            return null;
        }

        long duration = path.duration.get();
        KeyframeClip keyframe = new KeyframeClip();

        keyframe.copy(path);
        KeyframeInterpolation pos = path.interpolationPoint.get().interp;
        KeyframeInterpolation angle = path.interpolationAngle.get().interp;
        KeyframeEasing posEasing = path.interpolationPoint.get().easing;
        KeyframeEasing angleEasing = path.interpolationAngle.get().easing;

        long x;

        for (int i = 0; i < path.size(); i++)
        {
            Position point = path.points.get(i);

            x = (int) (i / (c - 1F) * duration);

            int index = keyframe.x.insert(x, (float) point.point.x);
            keyframe.y.insert(x, (float) point.point.y);
            keyframe.z.insert(x, (float) point.point.z);
            keyframe.yaw.insert(x, point.angle.yaw);
            keyframe.pitch.insert(x, point.angle.pitch);
            keyframe.roll.insert(x, point.angle.roll);
            keyframe.fov.insert(x, point.angle.fov);

            keyframe.x.get(index).setInterpolation(pos, posEasing);
            keyframe.y.get(index).setInterpolation(pos, posEasing);
            keyframe.z.get(index).setInterpolation(pos, posEasing);
            keyframe.yaw.get(index).setInterpolation(angle, angleEasing);
            keyframe.pitch.get(index).setInterpolation(angle, angleEasing);
            keyframe.roll.get(index).setInterpolation(angle, angleEasing);
            keyframe.fov.get(index).setInterpolation(angle, angleEasing);
        }

        return keyframe;
    }
}