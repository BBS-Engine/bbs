package mchorse.bbs.recording.data;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
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
    public List<Frame> frames = new ArrayList<>();
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
        Frame frame = this.frames.get(tick);

        if (frame != null)
        {
            return CollectionUtils.inRange(frame.actions, index) ? frame.actions.get(index) : null;
        }

        return null;
    }

    public Frame getFrame(int tick)
    {
        return tick >= 0 && tick < this.frames.size() ? this.frames.get(tick) : null;
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
        Frame frame = this.getFrame(tick);

        if (frame != null)
        {
            for (Action action : frame.actions)
            {
                if (safe && !action.isSafe())
                {
                    continue;
                }

                try
                {
                    action.apply(target);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reset the actor based on this record
     */
    public void reset(Entity actor)
    {}

    @Override
    public void toData(MapType data)
    {
        ListType frames = new ListType();

        /* Version of the recording */
        data.putShort("version", SIGNATURE);
        data.put("keyframes", this.keyframes.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        this.frames.clear();

        this.version = data.getShort("version");
        this.keyframes.fromData(data.getMap("keyframes"));
    }

    public void copy(Record data)
    {
        this.dirty = false;
        this.frames = data.frames;
    }
}