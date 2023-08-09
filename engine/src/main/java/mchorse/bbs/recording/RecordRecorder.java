package mchorse.bbs.recording;

import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

public class RecordRecorder
{
    /**
     * Initial record
     */
    public Record record;

    /**
     * Recording mode (record actions, frames or both)
     */
    public Mode mode;

    /**
     * Current recording tick
     */
    public int tick = 0;

    /**
     * Recording offset
     */
    public int offset = 0;

    /**
     * Groups that has to be recorded
     */
    public List<String> groups;

    public RecordRecorder(Record record, Mode mode, List<String> groups)
    {
        this.record = record;
        this.mode = mode;
        this.groups = groups;
    }

    /**
     * Record frame from the player
     */
    public void record(Entity player)
    {
        this.record.keyframes.record(this.tick, player, this.groups);

        this.tick++;
    }

    public void stop(Entity player)
    {
        this.record.length = this.tick;
    }
}