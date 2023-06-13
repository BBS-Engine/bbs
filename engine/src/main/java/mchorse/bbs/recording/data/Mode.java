package mchorse.bbs.recording.data;

/**
 * Mode enumeration. This enumeration represents how to playback the
 * record. Not really sure if BOTH is going to be used at all, but ACTIONS
 * and FRAMES definitely would.
 */
public enum Mode
{
    ACTIONS(true, false), FRAMES(false, true), BOTH(true, true);

    private final boolean actions;
    private final boolean frames;

    private Mode(boolean actions, boolean frames)
    {
        this.actions = actions;
        this.frames = frames;
    }

    public boolean isActions()
    {
        return this.actions;
    }

    public boolean isFrames()
    {
        return this.frames;
    }
}