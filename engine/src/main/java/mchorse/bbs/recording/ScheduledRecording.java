package mchorse.bbs.recording;

import mchorse.bbs.world.entities.Entity;

/**
 * Scheduled recorder class
 */
public class ScheduledRecording
{
    public RecordRecorder recorder;
    public Entity player;
    public Runnable runnable;
    public int countdown;
    public int offset;

    public ScheduledRecording(RecordRecorder recorder, Entity player, Runnable runnable, int countdown, int offset)
    {
        this.recorder = recorder;
        this.player = player;
        this.runnable = runnable;
        this.countdown = countdown;
        this.offset = offset;
    }

    public void run()
    {
        RecordUtils.setRecorder(this.player, this.recorder);

        if (this.runnable != null)
        {
            this.runnable.run();
        }
    }
}