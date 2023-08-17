package mchorse.bbs.recording;

import mchorse.bbs.BBSData;
import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;

import java.util.List;
import java.util.Map;

public class RecordRecorder
{
    public Record record;
    public Mode mode;
    public int tick = 0;
    public List<String> groups;

    public RecordRecorder(Record record, Mode mode, List<String> groups)
    {
        this.record = record;
        this.mode = mode;
        this.groups = groups;
    }

    public void record(Entity player)
    {
        this.record.keyframes.record(this.tick, player, this.groups);

        this.tick++;
    }

    public void stop(Entity player)
    {
        this.record.length.set(this.tick);

        if (this.groups == null || this.groups.isEmpty())
        {
            return;
        }

        this.copyOldKeyframes();
    }

    private void copyOldKeyframes()
    {
        Record oldRecord = BBSData.getRecords().load(this.record.getId());

        if (oldRecord == null)
        {
            return;
        }

        for (Clip clip : oldRecord.clips.get())
        {
            this.record.clips.add(clip);
        }

        Map<String, KeyframeChannel> map = this.record.keyframes.getMap();

        for (String key : map.keySet())
        {
            KeyframeChannel channel = map.get(key);

            if (channel.isEmpty())
            {
                channel.copy(oldRecord.keyframes.getMap().get(key));
            }
        }
    }
}