package mchorse.bbs.recording;

import mchorse.bbs.BBSData;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class RecordRecorder
{
    /**
     * Initial record
     */
    public Record record;

    private List<Action> actions = new ArrayList<Action>();

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

    public void addAction(Action action)
    {
        this.actions.add(action);
    }

    /**
     * Record frame from the player
     */
    public void record(Entity player)
    {
        if (this.mode.isActions())
        {
            Frame frame = new Frame();

            frame.fromEntity(player);
            this.record.frames.add(frame);
        }

        if (this.mode.isFrames())
        {
            Frame frame = this.record.getFrame(this.tick);

            if (frame != null)
            {
                frame.actions.addAll(this.actions);
            }

            this.actions.clear();
        }

        this.tick++;
    }

    public void stop(Entity player)
    {
        if (this.groups == null || this.groups.isEmpty())
        {
            return;
        }

        Record oldRecord = BBSData.getRecords().load(this.record.getId());

        if (oldRecord == null)
        {
            return;
        }

        for (int i = 0, c = Math.max(this.record.size(), oldRecord.size()); i < c; i++)
        {
            Frame frame = this.record.getFrame(i);
            Frame oldFrame = oldRecord.getFrame(i);

            if (frame == null)
            {
                this.record.frames.add(oldFrame);
            }
            else if (oldFrame != null)
            {
                Frame tmp = frame.copy();

                frame.copy(oldFrame, null);
                frame.copy(tmp, this.groups);
                frame.copyActions(oldFrame);
            }
        }
    }
}