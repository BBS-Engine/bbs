package mchorse.bbs.audio;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.resources.Link;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class SoundBuffer implements IDisposable
{
    private final Link id;
    private int buffer;
    private float duration;
    private Waveform waveform;

    public SoundBuffer(Link id, Wave wave, Waveform waveform)
    {
        this.id = id;

        this.buffer = AL10.alGenBuffers();
        ByteBuffer buffer = MemoryUtil.memAlloc(wave.data.length);

        buffer.put(wave.data);
        buffer.flip();

        AL10.alBufferData(this.buffer, wave.getALFormat(), buffer, wave.sampleRate);

        MemoryUtil.memFree(buffer);

        this.duration = wave.getDuration();
        this.waveform = waveform;
    }

    public Link getId()
    {
        return this.id;
    }

    public int getBuffer()
    {
        return this.buffer;
    }

    public float getDuration()
    {
        return this.duration;
    }

    public Waveform getWaveform()
    {
        return this.waveform;
    }

    @Override
    public void delete()
    {
        AL10.alDeleteBuffers(this.buffer);

        this.buffer = -1;

        if (this.waveform != null)
        {
            this.waveform.delete();
        }
    }
}