package mchorse.bbs.audio;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.watchdog.IWatchDogListener;
import mchorse.bbs.utils.watchdog.WatchDogEvent;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTDisconnect;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoundManager implements IDisposable, IWatchDogListener
{
    private static final float[] BUFFER = new float[6];

    private long device;
    private long context;

    private AssetProvider provider;
    private Map<Link, SoundBuffer> buffers = new HashMap<Link, SoundBuffer>();
    private List<SoundPlayer> sounds = new ArrayList<SoundPlayer>();

    private long lastConnectivityCheck;

    private Vector3f at = new Vector3f();
    private Vector3f up = new Vector3f();

    public SoundManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    public Collection<SoundPlayer> getPlayers()
    {
        return this.sounds;
    }

    public boolean isDevicePresent()
    {
        return this.device != MemoryUtil.NULL;
    }

    public void init()
    {
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);

        if (this.device == MemoryUtil.NULL)
        {
            this.device = 0;
            this.context = 0;

            return;
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(this.device);

        this.context = ALC10.alcCreateContext(device, (IntBuffer) null);

        if (this.context == MemoryUtil.NULL)
        {
            throw new IllegalStateException("There was an error creating an OpenAL context!");
        }

        ALC10.alcMakeContextCurrent(this.context);
        AL.createCapabilities(deviceCaps);

        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
    }

    /**
     * Load a sound buffer (optionally include a waveform).
     */
    public SoundBuffer load(Link link, boolean includeWaveform)
    {
        if (!this.isDevicePresent())
        {
            return null;
        }

        try
        {
            Wave wave = AudioReader.read(this.provider, link);
            Waveform waveform = null;

            if (includeWaveform)
            {
                if (wave.getBytesPerSample() > 2)
                {
                    wave = wave.convertTo16();
                }

                waveform = new Waveform();
                waveform.generate(wave, BBSSettings.audioWaveformDensity.get(), 40);
            }

            SoundBuffer buffer = new SoundBuffer(link, wave, waveform);

            this.buffers.put(link, buffer);

            System.out.println("Sound \"" + link + "\" was loaded!");

            return buffer;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public SoundBuffer get(Link link, boolean includeWaveform)
    {
        if (!this.isDevicePresent())
        {
            return null;
        }

        if (!this.buffers.containsKey(link))
        {
            return this.load(link, includeWaveform);
        }

        SoundBuffer player = this.buffers.get(link);

        if (includeWaveform && player.getWaveform() == null)
        {
            player.delete();

            return this.load(link, true);
        }

        return player;
    }

    public SoundPlayer play(Link link)
    {
        if (!this.isDevicePresent())
        {
            return null;
        }

        SoundBuffer buffer = this.get(link, false);

        if (buffer != null)
        {
            SoundPlayer player = new SoundPlayer(buffer);

            player.play();
            this.sounds.add(player);

            return player;
        }

        return null;
    }

    public SoundPlayer playUnique(Link link)
    {
        if (!this.isDevicePresent())
        {
            return null;
        }

        for (SoundPlayer player : this.sounds)
        {
            if (player.isUnique() && player.getBuffer().getId().equals(link))
            {
                return player;
            }
        }

        SoundBuffer buffer = this.get(link, true);

        if (buffer != null)
        {
            SoundPlayer player = new SoundPlayer(buffer).unique();

            player.play();
            this.sounds.add(player);

            return player;
        }

        return null;
    }

    public void stop(Link link)
    {
        Iterator<SoundPlayer> it = this.sounds.iterator();

        while (it.hasNext())
        {
            SoundPlayer player = it.next();

            if (player.getBuffer().getId().equals(link))
            {
                player.stop();
                player.delete();

                it.remove();
            }
        }
    }

    private void checkAudio()
    {
        long current = System.currentTimeMillis();

        if (current - this.lastConnectivityCheck < 1000)
        {
            return;
        }

        if (!this.isDevicePresent())
        {
            this.init();

            this.lastConnectivityCheck = current;

            return;
        }

        int connectivity = ALC10.alcGetInteger(this.device, EXTDisconnect.ALC_CONNECTED);

        if (connectivity == AL10.AL_FALSE)
        {
            this.init();
            this.delete();
        }

        this.lastConnectivityCheck = current;
    }

    /* Updating methods (general update, update position, velocity and orientation) */

    public void update()
    {
        this.checkAudio();

        Iterator<SoundPlayer> it = this.sounds.iterator();

        while (it.hasNext())
        {
            SoundPlayer player = it.next();

            if (player.canBeRemoved())
            {
                player.delete();
                it.remove();
            }
        }
    }

    public void setPosition(Vector3d vector)
    {
        this.setPosition((float) vector.x, (float) vector.y, (float) vector.z);
    }

    public void setPosition(float x, float y, float z)
    {
        AL10.alListener3f(AL10.AL_POSITION, x, y, z);
    }

    public void setVelocity(Vector3f vector)
    {
        this.setVelocity(vector.x, vector.y, vector.z);
    }

    public void setVelocity(double x, double y, double z)
    {
        this.setVelocity((float) x, (float) y, (float) z);
    }

    public void setVelocity(float x, float y, float z)
    {
        AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
    }

    public void setOrientation(Camera camera)
    {
        Matrix4f transform = camera.updateView();
        Vector3f at = this.at.set(0, 0, 0);
        Vector3f up = this.up.set(0, 0, 0);

        transform.positiveZ(at).negate();
        transform.positiveY(up);

        BUFFER[0] = at.x;
        BUFFER[1] = at.y;
        BUFFER[2] = at.z;
        BUFFER[3] = up.x;
        BUFFER[4] = up.y;
        BUFFER[5] = up.z;

        AL10.alListenerfv(AL10.AL_ORIENTATION, BUFFER);
    }

    @Override
    public void delete()
    {
        this.deleteSounds();

        if (this.isDevicePresent())
        {
            ALC10.alcDestroyContext(this.context);
            ALC10.alcCloseDevice(this.device);
            ALC.destroy();
        }

        this.device = 0;
        this.context = 0;
    }

    public void deleteSounds()
    {
        for (SoundPlayer player : this.sounds)
        {
            player.delete();
        }

        this.sounds.clear();

        for (SoundBuffer buffer : this.buffers.values())
        {
            buffer.delete();
        }

        this.buffers.clear();
    }

    /* Watch dog listener implementation */

    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        if (!Files.isRegularFile(path))
        {
            return;
        }

        String relativePath = IWatchDogListener.getAssetsLink(path).path;

        if (relativePath.endsWith(".ogg") || relativePath.endsWith(".wav"))
        {
            if (relativePath.startsWith("/"))
            {
                relativePath = relativePath.substring(1);
            }

            Link link = Link.assets(relativePath);

            if (this.buffers.containsKey(link))
            {
                this.stop(link);

                SoundBuffer buffer = this.buffers.remove(link);

                if (buffer != null)
                {
                    buffer.delete();
                }
            }
        }
    }
}