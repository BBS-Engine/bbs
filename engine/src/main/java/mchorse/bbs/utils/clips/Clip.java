package mchorse.bbs.utils.clips;

import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.utils.clips.values.ValueEnvelope;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueString;

public abstract class Clip extends StructureBase
{
    public final ValueBoolean enabled = new ValueBoolean("enabled", true);
    public final ValueString title = new ValueString("title", "");
    public final ValueInt color = new ValueInt("color", 0);
    public final ValueInt layer = new ValueInt("layer", 0, 0, Integer.MAX_VALUE);
    public final ValueInt tick = new ValueInt("tick", 0, 0, Integer.MAX_VALUE);
    public final ValueInt duration = new ValueInt("duration", 1, 1, Integer.MAX_VALUE);
    public final ValueEnvelope envelope = new ValueEnvelope("envelope");

    public Clip()
    {
        this.register(this.enabled);
        this.register(this.title);
        this.register(this.color);
        this.register(this.layer);
        this.register(this.tick);
        this.register(this.duration);
        this.register(this.envelope);
    }

    public boolean isGlobal()
    {
        return false;
    }

    public boolean isInside(int tick)
    {
        int offset = this.tick.get();

        return tick >= offset && tick < offset + this.duration.get();
    }

    public Clip copy()
    {
        Clip clip = this.create();

        clip.copy(this);

        return clip;
    }

    protected abstract Clip create();

    /**
     * Breakdown this fixture into another piece starting at given offset
     */
    public Clip breakDown(int offset)
    {
        int duration = this.duration.get();

        if (offset <= 0 || offset >= duration)
        {
            return null;
        }

        Clip clip = this.copy();

        clip.duration.set(duration - offset);
        clip.breakDownClip(this, offset);

        return clip;
    }

    protected void breakDownClip(Clip original, int offset)
    {
        this.envelope.get().breakDown(original, offset);
    }
}