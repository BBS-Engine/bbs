package mchorse.bbs.ui.film.screenplay;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.ColorCode;
import mchorse.bbs.audio.SoundBuffer;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.audio.Wave;
import mchorse.bbs.audio.Waveform;
import mchorse.bbs.audio.wav.WaveReader;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IUITreeEventListener;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class UIAudioPlayer extends UIElement implements IUITreeEventListener
{
    public static final float PIXELS = 40;

    public UIIcon play;

    private Wave wave;
    private Waveform waveform;
    private SoundBuffer buffer;
    private SoundPlayer player;

    private boolean wasPlaying;

    public UIAudioPlayer()
    {
        this.play = new UIIcon(Icons.PLAY, (b) -> this.togglePlaying());
        this.play.relative(this).h(1F);

        this.add(this.play);
    }

    public Wave getWave()
    {
        return this.wave;
    }

    @Override
    public void onAddedToTree(UIElement element)
    {}

    @Override
    public void onRemovedFromTree(UIElement element)
    {
        this.delete();
    }

    public void delete()
    {
        if (this.waveform != null) this.waveform.delete();
        if (this.buffer != null) this.buffer.delete();
        if (this.player != null) this.player.delete();

        this.wave = null;
        this.waveform = null;
        this.buffer = null;
        this.player = null;
    }

    public void loadAudio(File file)
    {
        this.loadAudio(file, 0F);
    }

    public void loadAudio(File file, float cutoff)
    {
        if (file == null || !file.exists())
        {
            this.delete();
        }
        else
        {
            try
            {
                this.loadAudio(new WaveReader().read(new FileInputStream(file)), null, cutoff);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void loadAudio(Wave wave)
    {
        this.loadAudio(wave, null, 0F);
    }

    public void loadAudio(Wave wave, List<ColorCode> colorCodes)
    {
        this.loadAudio(wave, colorCodes, 0F);
    }

    public void loadAudio(Wave wave, List<ColorCode> colorCodes, float cutoff)
    {
        this.wave = wave;
        this.waveform = new Waveform();

        if (cutoff != 0)
        {
            float newLength = Math.max(wave.getDuration() - Math.abs(cutoff), 0);

            if (newLength > 0)
            {
                int newLengthBytes = (int) (newLength * wave.byteRate);

                newLengthBytes -= newLengthBytes % wave.getBytesPerSample();

                int newOffset = cutoff < 0 ? wave.data.length - newLengthBytes : 0;
                byte[] bytes = new byte[newLengthBytes];

                System.arraycopy(wave.data, newOffset, bytes, 0, newLengthBytes);

                wave.data = bytes;
            }
        }

        this.waveform.generate(this.wave, colorCodes, (int) PIXELS, 20);

        this.buffer = new SoundBuffer(null, this.wave, this.waveform);
        this.player = new SoundPlayer(this.buffer);

        this.player.setRelative(true);
        this.player.stop();
    }

    private void togglePlaying()
    {
        if (this.player != null)
        {
            if (this.player.isPlaying())
            {
                this.player.pause();
            }
            else
            {
                this.player.play();
            }

            this.updatePlayIcon();

            this.wasPlaying = this.player.isPlaying();
        }
    }

    private void updatePlayIcon()
    {
        this.play.both(this.player.isPlaying() ? Icons.PAUSE : Icons.PLAY);
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        Area.SHARED.set(this.area.x + 20, this.area.y, this.area.w - 20, this.area.h);

        if (this.player != null && Area.SHARED.isInside(context) && context.mouseButton == 0)
        {
            float playback = this.player.getPlaybackPosition();
            float offset = playback > 2F ? playback - 2F : 0F;
            float newPlayback = (context.mouseX - (this.area.x + 20)) / PIXELS;

            this.player.setPlaybackPosition(newPlayback + offset);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A75);

        if (this.waveform != null)
        {
            int w = this.area.w - 20;
            float playback = this.player.getPlaybackPosition();
            float offset = playback > 2F ? playback - 2F : 0F;

            this.waveform.render(context.batcher, Colors.WHITE, this.area.x + 20, this.area.y, w, this.area.h, offset, offset + w / PIXELS);

            int x = this.area.x + 20 + (int) (playback * this.waveform.getPixelsPerSecond() - offset * PIXELS);

            context.batcher.box(x, this.area.y, x + 1, this.area.ey(), Colors.CURSOR);

            int color = BBSSettings.primaryColor(Colors.A50);
            String label = String.format("%.1f/%.1f", this.player.getPlaybackPosition(), this.player.getBuffer().getDuration());

            context.batcher.textCard(context.font, label, this.area.ex() - 5 - context.font.getWidth(label), this.area.y + (this.area.h - context.font.getHeight()) / 2, Colors.WHITE, color);
        }

        if (this.player != null && this.wasPlaying != this.player.isPlaying())
        {
            this.wasPlaying = this.player.isPlaying();

            this.updatePlayIcon();
        }

        super.render(context);
    }
}