package mchorse.bbs.ui.film.screenplay;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.ColorCode;
import mchorse.bbs.audio.Wave;
import mchorse.bbs.audio.wav.WaveWriter;
import mchorse.bbs.camera.clips.misc.AudioClip;
import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.camera.clips.misc.VoicelineClip;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.film.Film;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.UIClipsPanel;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.SyntaxStyle;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageFolderOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIScreenplayEditor extends UIElement
{
    public UIElement masterBar;
    public UIAudioPlayer master;
    public UIIcon generate;
    public UIIcon subtitles;
    public UIIcon save;

    public UIClipsPanel editor;

    private UIFilmPanel panel;
    private Film film;
    private SyntaxStyle style = new SyntaxStyle();
    private List<ColorCode> colorCodes = new ArrayList<>();

    public UIScreenplayEditor(UIFilmPanel panel)
    {
        this.panel = panel;

        this.master = new UIAudioPlayer();
        this.generate = new UIIcon(Icons.SOUND, (b) -> this.generate());
        this.generate.tooltip(UIKeys.VOICE_LINE_COMPILE);
        this.subtitles = new UIIcon(Icons.FONT, (b) -> this.generateSubtitles());
        this.subtitles.tooltip(UIKeys.VOICE_LINE_SUBTITLES);
        this.save = new UIIcon(Icons.SAVED, (b) -> this.saveAudio());
        this.save.tooltip(UIKeys.VOICE_LINE_SAVE);
        this.masterBar = UI.row(this.master, this.save, this.subtitles, this.generate);
        this.masterBar.relative(this).x(10).y(10).w(1F, -20).h(20);

        this.editor = new UIClipsPanel(panel, BBS.getFactoryScreenplayClips());
        this.editor.relative(this).y(40).w(1F).h(1F, -40);

        this.add(this.editor, this.masterBar);
        this.markContainer();
    }

    private void generate()
    {
        Wave lastWave = null;
        float total = this.film.voiceLines.calculateDuration() / 20F;
        Map<VoicelineClip, Wave> map = new HashMap<>();

        this.colorCodes.clear();

        for (Clip aClip : this.film.voiceLines.get())
        {
            if (!(aClip instanceof VoicelineClip))
            {
                continue;
            }

            VoicelineClip clip = (VoicelineClip) aClip;
            Wave wave = UIFilmPanel.getVoiceLines().get(clip).a;

            if (wave != null)
            {
                map.put(clip, wave);
            }
        }

        int totalBytes = (int) (total * map.values().iterator().next().byteRate);
        byte[] bytes = new byte[totalBytes + totalBytes % 2];
        ByteBuffer buffer = MemoryUtil.memAlloc(2);

        for (Clip aClip : this.film.voiceLines.get())
        {
            if (!(aClip instanceof VoicelineClip))
            {
                continue;
            }

            VoicelineClip clip = (VoicelineClip) aClip;

            try
            {
                float time = clip.tick.get() / 20F;
                float duration = clip.duration.get() / 20F;
                Wave wave = map.get(clip);

                int offset = (int) (time * wave.byteRate);
                int length = (int) (duration * wave.byteRate);

                offset -= offset % 2;

                length = Math.min(wave.data.length, MathUtils.clamp(length, 0, bytes.length - offset));
                length -= length % 2;

                for (int i = 0; i < length; i += 2)
                {
                    buffer.position(0);
                    buffer.put(wave.data[i]);
                    buffer.put(wave.data[i + 1]);

                    int waveShort = buffer.getShort(0);

                    buffer.position(0);
                    buffer.put(bytes[offset + i]);
                    buffer.put(bytes[offset + i + 1]);

                    int bytesShort = buffer.getShort(0);
                    int finalShort = waveShort + bytesShort;

                    buffer.putShort(0, (short) MathUtils.clamp(finalShort, Short.MIN_VALUE, Short.MAX_VALUE));

                    bytes[offset + i + 1] = buffer.get(1);
                    bytes[offset + i] =     buffer.get(0);
                }

                this.colorCodes.add(new ColorCode(time, time + duration, BBSSettings.elevenVoiceColors.getColor(clip.voice.get())));

                lastWave = wave;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        MemoryUtil.memFree(buffer);

        if (lastWave != null)
        {
            Wave wave = new Wave(lastWave.audioFormat, lastWave.numChannels, lastWave.sampleRate, lastWave.bitsPerSample, bytes);

            this.master.loadAudio(wave, this.colorCodes);
        }
    }

    private void generateSubtitles()
    {
        Film data = this.panel.getData();
        int layer = data.camera.getTopLayer();

        for (Clip aClip : this.film.voiceLines.get())
        {
            if (!(aClip instanceof VoicelineClip))
            {
                continue;
            }

            VoicelineClip action = (VoicelineClip) aClip;
            SubtitleClip clip = new SubtitleClip();

            clip.title.set(action.content.get());
            clip.tick.set(action.tick.get());
            clip.duration.set(action.duration.get());
            clip.layer.set(layer + 1);
            clip.color.set(BBSSettings.elevenVoiceColors.getColor(action.voice.get()));

            data.camera.addClip(clip);
        }

        this.panel.showPanel(this.panel.cameraClips);
    }

    private void saveAudio()
    {
        Wave wave = this.master.getWave();

        if (wave == null)
        {
            return;
        }

        try
        {
            File folder = BBS.getAssetsPath("audio");
            String filename = this.panel.getData().getId() + ".wav";

            WaveWriter.write(new File(folder, filename), wave);
            ListType colorCodes = new ListType();

            for (ColorCode colorCode : this.colorCodes)
            {
                colorCodes.add(colorCode.toData());
            }

            DataToString.writeSilently(new File(folder, filename + ".json"), colorCodes, true);

            if (Window.isCtrlPressed())
            {
                Film film = this.panel.getFilm();
                int layer = film.camera.getTopLayer();

                AudioClip clip = new AudioClip();

                clip.duration.set((int) (wave.getDuration() * 20));
                clip.layer.set(layer + 1);
                clip.audio.set(Link.assets("audio/" + filename));

                film.camera.addClip(clip);
                this.panel.showPanel(this.panel.cameraClips);
            }
            else
            {
                UIOverlay.addOverlay(this.getContext(), new UIMessageFolderOverlayPanel(
                    UIKeys.VOICE_LINE_SAVE_AUDIO_TITLE,
                    UIKeys.VOICE_LINE_SAVE_AUDIO_DESCRIPTION.format(filename),
                    folder
                ));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setFilm(Film film)
    {
        this.film = film;

        this.fillData();
    }

    private void fillData()
    {
        this.editor.clips.setClips(this.film.voiceLines);

        this.resize();
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, this.style.background | Colors.A100);

        super.render(context);
    }
}