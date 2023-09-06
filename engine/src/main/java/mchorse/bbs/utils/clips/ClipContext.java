package mchorse.bbs.utils.clips;

import mchorse.bbs.bridge.IBridge;

import java.util.HashMap;
import java.util.Map;

public abstract class ClipContext <T extends Clip, E>
{
    /**
     * Tick since the beginning of the camera profile.
     */
    public int ticks;

    /**
     * Tick relative to beginning of the camera clip.
     */
    public int relativeTick;

    /**
     * Transition between update ticks.
     */
    public float transition;

    /**
     * Current layer.
     */
    public int currentLayer;

    /**
     * Current clips
     */
    public Clips clips;

    /**
     * Whether currently camera is played or paused
     */
    public boolean playing = true;

    public Map<String, Object> clipData = new HashMap<>();

    public IBridge bridge;

    public ClipContext setup(int ticks, float transition)
    {
        return this.setup(ticks, ticks, transition);
    }

    public ClipContext setup(int ticks, int relativeTick, float transition)
    {
        return this.setup(ticks, relativeTick, transition, 0);
    }

    public ClipContext setup(int ticks, int relativeTick, float transition, int currentLayer)
    {
        this.ticks = ticks;
        this.relativeTick = relativeTick;
        this.transition = transition;
        this.currentLayer = currentLayer;

        return this;
    }

    public abstract boolean apply(Clip clip, E position);

    /**
     * Apply clips underneath currently running
     */
    public boolean applyUnderneath(int ticks, float transition, E position)
    {
        if (this.currentLayer > 0)
        {
            int lastLayer = this.currentLayer;
            int lastTicks = this.ticks;
            int lastRelativeTicks = this.relativeTick;
            float lastTransition = this.transition;

            this.ticks = ticks;
            this.transition = transition;

            boolean applied = false;

            for (Clip clip : this.clips.getClips(ticks, lastLayer))
            {
                if (this.apply(clip, position))
                {
                    applied = true;
                }
            }

            this.currentLayer = lastLayer;
            this.ticks = lastTicks;
            this.relativeTick = lastRelativeTicks;
            this.transition = lastTransition;

            return applied;
        }

        return false;
    }
}