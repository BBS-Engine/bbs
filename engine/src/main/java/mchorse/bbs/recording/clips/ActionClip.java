package mchorse.bbs.recording.clips;

import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.world.entities.Entity;

public abstract class ActionClip extends Clip
{
    public void apply(Entity actor, int offset, boolean playing)
    {}
}