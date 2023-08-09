package mchorse.bbs.recording.data;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.recording.values.ValueFrames;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.utils.clips.values.ValueClips;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

public class Record extends StructureBase
{
    public final ValueInt length = new ValueInt("length", 0);
    public final ValueFrames keyframes = new ValueFrames("keyframes");
    public final ValueClips clips = new ValueClips("clips", BBS.getFactoryActions());

    public Record()
    {
        this.register(this.length);
        this.register(this.keyframes);
        this.register(this.clips);
    }

    public Record(String id)
    {
        this.setId(id);
    }

    public int getLength()
    {
        return this.length.get();
    }

    public void applyFrame(int tick, Entity actor)
    {
        this.applyFrame(tick, actor, null);
    }

    public void applyFrame(int tick, Entity actor, List<String> groups)
    {
        this.keyframes.apply(tick, actor, groups);
    }

    public void applyAction(int tick, Entity target)
    {
        /* TODO: implement clips as actions */
    }
}