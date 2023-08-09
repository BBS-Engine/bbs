package mchorse.bbs.recording.data;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

/**
 * This class stores actions and frames states for a recording (to be played
 * back or while recording).
 *
 * There's two list arrays in this class, index in both of these arrays
 * represents the frame position (0 is first frame). Frames list is always
 * populated, but actions list will contain some nulls.
 */
public class Record extends AbstractData
{
    public static final short SIGNATURE = 2;

    public short version = SIGNATURE;
    public boolean dirty;

    public int length;
    public Frames keyframes = new Frames();

    public Record()
    {}

    public Record(String id)
    {
        this.setId(id);
    }

    public int size()
    {
        return this.length;
    }

    public Action getAction(int tick, int index)
    {
        return null;
    }

    public void applyFrame(int tick, Entity actor)
    {
        this.applyFrame(tick, actor, null);
    }

    /**
     * Apply a frame at given tick on the given actor.
     */
    public void applyFrame(int tick, Entity actor, List<String> groups)
    {
        this.keyframes.apply(tick, actor, groups);
    }

    public void applyAction(int tick, Entity target)
    {
        this.applyAction(tick, target, false);
    }

    public void applyAction(int tick, Entity target, boolean safe)
    {

    }

    /**
     * Reset the actor based on this record
     */
    public void reset(Entity actor)
    {}

    @Override
    public void toData(MapType data)
    {
        data.putShort("version", SIGNATURE);
        data.putInt("length", this.length);
        data.put("keyframes", this.keyframes.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        this.version = data.getShort("version");
        this.length = data.getInt("length");
        this.keyframes.fromData(data.getMap("keyframes"));
    }

    public void copy(Record data)
    {
        this.dirty = false;

        this.length = data.length;
        this.keyframes.copy(data.keyframes);
    }
}