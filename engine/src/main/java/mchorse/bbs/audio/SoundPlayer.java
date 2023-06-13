package mchorse.bbs.audio;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class SoundPlayer implements IDisposable
{
    private int source;
    private SoundBuffer buffer;
    private boolean unique;

    public SoundPlayer(SoundBuffer buffer)
    {
        this.buffer = buffer;
        this.source = AL10.alGenSources();

        AL10.alSourcei(this.source, AL10.AL_BUFFER, buffer.getBuffer());
        AL10.alSourcef(this.source, AL10.AL_MAX_DISTANCE, 60);

        this.setRelative(false);
    }

    public SoundPlayer unique()
    {
        this.unique = true;

        return this;
    }

    public int getSource()
    {
        return this.source;
    }

    public SoundBuffer getBuffer()
    {
        return this.buffer;
    }

    public boolean isUnique()
    {
        return this.unique;
    }

    public boolean canBeRemoved()
    {
        return !this.unique && this.isStopped();
    }

    /* Properties */

    public void setVolume(float volume)
    {
        AL10.alSourcef(this.source, AL10.AL_GAIN, volume);
    }

    public void setPitch(float pitch)
    {
        AL10.alSourcef(this.source, AL10.AL_PITCH, pitch);
    }

    public void setRelative(boolean relative)
    {
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, relative ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public void setLooping(boolean looping)
    {
        AL10.alSourcei(this.source, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public void setPosition(Vector3f vector)
    {
        this.setPosition(vector.x, vector.y, vector.z);
    }

    public void setPosition(float x, float y, float z)
    {
        AL10.alSource3f(this.source, AL10.AL_POSITION, x, y, z);
    }

    public void setVelocity(Vector3f vector)
    {
        this.setVelocity(vector.x, vector.y, vector.z);
    }

    public void setVelocity(float x, float y, float z)
    {
        AL10.alSource3f(this.source, AL10.AL_VELOCITY, x, y, z);
    }

    /* Playback */

    public void play()
    {
        AL10.alSourcePlay(this.source);
    }

    public void pause()
    {
        AL10.alSourcePause(this.source);
    }

    public void stop()
    {
        AL10.alSourceStop(this.source);
    }

    public int getSourceState()
    {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE);
    }

    public boolean isPlaying()
    {
        return this.getSourceState() == AL10.AL_PLAYING;
    }

    public boolean isPaused()
    {
        return this.getSourceState() == AL10.AL_PAUSED;
    }

    public boolean isStopped()
    {
        if (this.source == -1)
        {
            return true;
        }

        int state = this.getSourceState();

        return state == AL10.AL_STOPPED || state == AL10.AL_INITIAL;
    }

    public float getPlaybackPosition()
    {
        return AL10.alGetSourcef(this.source, AL11.AL_SEC_OFFSET);
    }

    public void setPlaybackPosition(float seconds)
    {
        seconds = MathUtils.clamp(seconds, 0, this.buffer.getDuration());

        AL10.alSourcef(this.source, AL11.AL_SEC_OFFSET, seconds);
    }

    @Override
    public void delete()
    {
        AL10.alDeleteSources(this.source);

        this.source = -1;
        this.buffer = null;
    }
}