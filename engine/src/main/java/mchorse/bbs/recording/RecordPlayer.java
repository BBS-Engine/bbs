package mchorse.bbs.recording;

import mchorse.bbs.BBSData;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.actions.FormAction;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Mode;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

/**
 * Record player class
 *
 * This thing is responsible for playing given record. It applies frames and
 * actions from the record instance on the given actor.
 */
public class RecordPlayer
{
    /**
     * Record from which this player is going to play
     */
    public Record record;

    /**
     * Play mode
     */
    public Mode mode;

    /**
     * Entity which is used by this record player to replay the action 
     */
    public Entity actor;

    /**
     * Current tick
     */
    public int tick = 0;

    /**
     * Whether to kill an actor when player finished playing
     */
    public boolean kill = false;

    /**
     * Is this player is playing
     */
    public boolean playing = true;

    /**
     * Sync mode - pauses the playback once hit the end
     */
    public boolean sync = false;

    /**
     * It might be null
     */
    public Replay replay;

    public List<String> groups;

    public RecordPlayer(Record record, Mode mode, Entity actor)
    {
        this.record = record;
        this.mode = mode;
        this.actor = actor;
    }

    /**
     * Check if the record player is finished
     */
    public boolean isFinished()
    {
        boolean isFinished = this.record != null && this.tick >= this.record.size();

        if (isFinished && this.sync)
        {
            if (this.playing)
            {
                this.pause();
            }

            return false;
        }

        return isFinished;
    }

    /**
     * Get appropriate amount of real ticks (for accessing current 
     * action or something like this)
     */
    public int getTick()
    {
        return Math.max(0, this.tick);
    }

    public void next()
    {
        this.next(this.actor);
    }

    /**
     * Apply current frame and advance to the next one
     */
    public void next(Entity actor)
    {
        if (!this.playing && this.sync)
        {
            this.applyFrame(this.tick, actor);

            return;
        }

        if (!this.playing || this.isFinished())
        {
            return;
        }

        if (this.record != null)
        {
            if (this.mode.isActions()) this.applyAction(this.tick, actor, false);
            if (this.mode.isFrames()) this.applyFrame(this.tick, actor);
        }

        this.tick++;
    }

    /**
     * Pause the playing actor
     */
    public void pause()
    {
        this.playing = false;
    }

    /**
     * Resume the paused actor
     */
    public void resume(int tick, Replay replay)
    {
        if (tick >= 0)
        {
            this.tick = tick;
        }

        this.playing = true;

        FormAction action = this.seekAction(tick - 1, FormAction.class);

        if (action == null)
        {
            replay.apply(this.actor);
        }
        else
        {
            action.apply(this.actor);
        }
    }

    /**
     * Make an actor go to the given tick
     */
    public void goTo(int tick, boolean actions, Replay replay)
    {
        int original = tick;

        if (tick > this.record.frames.size())
        {
            tick = this.record.frames.size() - 1;
        }

        int min = Math.min(this.tick, tick);
        int max = Math.max(this.tick, tick);

        if (actions)
        {
            for (int i = min; i < max; i++)
            {
                this.record.applyAction(i, this.actor);
            }
        }

        this.tick = original;

        this.record.applyFrame(tick, this.actor, this.groups);

        if (actions)
        {
            this.record.applyAction(tick, this.actor);

            FormAction action = this.seekAction(tick - 1, FormAction.class);

            if (action == null)
            {
                replay.apply(this.actor);
            }
            else
            {
                action.apply(this.actor);
            }
        }
    }

    public <T extends Action> T seekAction(int tick, Class<T> actionType)
    {
        while (tick >= 0)
        {
            Frame frame = this.record.getFrame(tick);

            if (frame == null)
            {
                return null;
            }

            if (!frame.actions.isEmpty())
            {
                for (int i = frame.actions.size() - 1; i >= 0; i--)
                {
                    Action action = frame.actions.get(i);

                    if (action.getClass() == actionType)
                    {
                        return actionType.cast(action);
                    }
                }
            }

            tick -= 1;
        }

        return null;
    }

    /**
     * Start the playback, but with default tick argument
     */
    public void startPlaying(boolean kill)
    {
        this.startPlaying(0, kill);
    }

    /**
     * Start the playback, invoked by director block (more specifically by
     * DirectorTileEntity).
     */
    public void startPlaying(int tick, boolean kill)
    {
        this.playing = true;
        this.tick = tick;
        this.kill = kill;
        this.sync = false;

        this.applyFrame(tick, this.actor);
        this.actor.basic.prevPosition.set(this.actor.basic.position);
        this.actor.basic.prevRotation.set(this.actor.basic.rotation);
        RecordUtils.setPlayer(this.actor, this);

        World world = this.actor.world;

        if (!world.entities.contains(this.actor))
        {
            world.addEntitySafe(this.actor);
        }

        this.actor.basic.manualControl = true;
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        this.actor.basic.manualControl = false;

        BBSData.getRecords().stop(this);
    }

    public void applyFrame(int tick, Entity target)
    {
        tick = MathUtils.clamp(tick, 0, this.record.frames.size() - 1);

        this.record.applyFrame(tick, target, this.groups);
    }

    public void applyAction(int tick, Entity target, boolean safe)
    {
        this.record.applyAction(tick, target, safe);
    }
}