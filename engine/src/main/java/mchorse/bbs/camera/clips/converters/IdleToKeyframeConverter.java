package mchorse.bbs.camera.clips.converters;

import mchorse.bbs.camera.clips.overwrite.IdleClip;
import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

public class IdleToKeyframeConverter implements IClipConverter<IdleClip, KeyframeClip>
{
    @Override
    public KeyframeClip convert(IdleClip clip)
    {
        KeyframeClip keyframeClip = new KeyframeClip();

        keyframeClip.copy(clip);

        this.insert(keyframeClip.x.get(), clip.position.get().point.x);
        this.insert(keyframeClip.y.get(), clip.position.get().point.y);
        this.insert(keyframeClip.z.get(), clip.position.get().point.z);
        this.insert(keyframeClip.yaw.get(), clip.position.get().angle.yaw);
        this.insert(keyframeClip.pitch.get(), clip.position.get().angle.pitch);
        this.insert(keyframeClip.roll.get(), clip.position.get().angle.roll);
        this.insert(keyframeClip.fov.get(), clip.position.get().angle.fov);

        return keyframeClip;
    }

    private void insert(KeyframeChannel channel, double value)
    {
        channel.getKeyframes().clear();
        channel.insert(0, value);
    }
}