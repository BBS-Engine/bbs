package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.settings.values.ValueInt;

public abstract class ComponentClip extends Clip
{
    /**
     * Active value that uses only 7 bits for determining which components
     * should be processed.
     */
    public final ValueInt active = new ValueInt("active", 0, 0, 0b11111111);

    public ComponentClip()
    {
        super();

        this.register(this.active);
    }

    /**
     * Whether current given bit is 1 
     */
    public boolean isActive(int bit)
    {
        return (this.active.get() >> bit & 1) == 1;
    }
}