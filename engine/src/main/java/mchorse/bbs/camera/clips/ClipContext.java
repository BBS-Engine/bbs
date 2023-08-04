package mchorse.bbs.camera.clips;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.data.Position;

import java.util.HashMap;
import java.util.Map;

public class ClipContext
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
     * Current camera work that the clip is associated with.
     */
    public CameraWork work;

    /**
     * Whether currently camera is played or paused
     */
    public boolean playing = true;

    public Map<String, Object> clipData = new HashMap<String, Object>();

    public IBridge bridge;

    public ClipContext setup(int ticks, int transition)
    {
        return this.setup(ticks, ticks, transition);
    }

    public ClipContext setup(int ticks, int relativeTick, int transition)
    {
        return this.setup(ticks, relativeTick, transition, 0);
    }

    public ClipContext setup(int ticks, int relativeTick, int transition, int currentLayer)
    {
        this.ticks = ticks;
        this.relativeTick = relativeTick;
        this.transition = transition;
        this.currentLayer = currentLayer;

        return this;
    }

    public void apply(CameraClip clip, Position position)
    {
        this.currentLayer = clip.layer.get();
        this.relativeTick = this.ticks - clip.tick.get();

        clip.apply(this, position);
    }

    /**
     * Apply clips underneath currently running
     */
    public boolean applyUnderneath(int ticks, float transition, Position position)
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

            for (Clip clip : this.work.clips.getClips(ticks, lastLayer))
            {
                if (clip instanceof CameraClip)
                {
                    this.apply((CameraClip) clip, position);

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

    public void shutdown()
    {
        if (this.work == null)
        {
            return;
        }

        for (Clip clip : this.work.clips.get())
        {
            if (clip instanceof CameraClip)
            {
                ((CameraClip) clip).shutdown(this);
            }
        }
    }
}