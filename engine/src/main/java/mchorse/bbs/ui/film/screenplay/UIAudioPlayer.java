package mchorse.bbs.ui.film.screenplay;

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
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class UIAudioPlayer extends UIElement implements IUITreeEventListener
{
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
        if (file == null || !file.exists())
        {
            this.delete();
        }
        else
        {
            try
            {
                this.loadAudio(new WaveReader().read(new FileInputStream(file)));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void loadAudio(Wave wave)
    {
        this.loadAudio(wave, null);
    }

    public void loadAudio(Wave wave, List<ColorCode> colorCodes)
    {
        this.wave = wave;
        this.waveform = new Waveform();

        this.waveform.generate(this.wave, colorCodes, 20, 20);

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
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A75);

        if (this.waveform != null)
        {
            int w = this.area.w - 20;

            this.waveform.render(context.batcher, Colors.WHITE, this.area.x + 20, this.area.y, w, this.area.h, 0, w / 20F);

            int x = this.area.x + 20 + (int) (this.player.getPlaybackPosition() * this.waveform.getPixelsPerSecond());

            context.batcher.box(x, this.area.y, x + 1, this.area.ey(), Colors.CURSOR);
        }

        if (this.player != null && this.wasPlaying != this.player.isPlaying())
        {
            this.wasPlaying = this.player.isPlaying();
            this.updatePlayIcon();
        }

        super.render(context);
    }
}