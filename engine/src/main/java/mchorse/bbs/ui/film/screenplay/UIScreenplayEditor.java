package mchorse.bbs.ui.film.screenplay;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.ColorCode;
import mchorse.bbs.audio.Wave;
import mchorse.bbs.audio.wav.WaveWriter;
import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.screenplay.Screenplay;
import mchorse.bbs.film.screenplay.ScreenplayAction;
import mchorse.bbs.film.tts.ElevenLabsAPI;
import mchorse.bbs.film.tts.ElevenLabsResult;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.SyntaxStyle;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageFolderOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.colors.Colors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIScreenplayEditor extends UIElement
{
    public UIElement masterBar;
    public UIAudioPlayer master;
    public UIIcon generate;
    public UIIcon subtitles;
    public UIIcon save;

    public UIScrollView editor;
    public UIIcon add;

    private UIFilmPanel panel;
    private Screenplay screenplay;
    private SyntaxStyle style = new SyntaxStyle();
    private List<ColorCode> colorCodes = new ArrayList<>();

    public UIScreenplayEditor(UIFilmPanel panel)
    {
        this.panel = panel;

        this.master = new UIAudioPlayer();
        this.generate = new UIIcon(Icons.SOUND, (b) -> this.generate());
        this.generate.tooltip(IKey.lazy("Compile generated audio"));
        this.subtitles = new UIIcon(Icons.FONT, (b) -> this.generateSubtitles());
        this.subtitles.tooltip(IKey.lazy("Generate subtitle clips"));
        this.save = new UIIcon(Icons.SAVED, (b) -> this.saveAudio());
        this.save.tooltip(IKey.lazy("Save compiled audio"));
        this.masterBar = UI.row(this.master, this.save, this.subtitles, this.generate);
        this.masterBar.relative(this).x(10).y(10).w(1F, -20).h(20);

        this.editor = UI.scrollView(15, 10);
        this.editor.relative(this).y(40).w(1F).h(1F, -40);
        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            this.editor.addBefore(this.add, this.createAction(this.screenplay.addAction()));
            this.resize();
        });
        this.add.tooltip(IKey.lazy("Add new action..."), Direction.TOP);

        this.add(this.editor, this.masterBar);
        this.markContainer();
    }

    private void generate()
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Wave lastWave = null;
        float offset = 0;

        this.colorCodes.clear();

        for (UIScreenplayAction uiAction : this.editor.getChildren(UIScreenplayAction.class))
        {
            Wave wave = uiAction.audioPlayer.getWave();

            if (wave == null)
            {
                continue;
            }

            ScreenplayAction action = uiAction.getAction();
            float pause = action.pause.get();
            int pauseBytes = (int) (Math.abs(pause) * wave.byteRate);

            pauseBytes -= pauseBytes % wave.getBytesPerSample();

            try
            {
                if (pause < 0)
                {
                    output.write(new byte[pauseBytes]);
                }

                output.write(wave.data);

                if (pause > 0)
                {
                    output.write(new byte[pauseBytes]);
                }

                float time = offset + (pause < 0 ? -pause : 0);
                float waveDuration = wave.getDuration();

                this.colorCodes.add(new ColorCode(time, time + waveDuration, BBSSettings.elevenVoiceColors.getColor(action.voice.get())));

                offset += Math.abs(pause) + waveDuration;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            lastWave = wave;
        }

        if (lastWave != null)
        {
            Wave wave = new Wave(lastWave.audioFormat, lastWave.numChannels, lastWave.sampleRate, lastWave.bitsPerSample, output.toByteArray());

            this.master.loadAudio(wave, this.colorCodes);
        }
    }

    private void generateSubtitles()
    {
        Film data = this.panel.getData();
        float offset = 0;
        int layer = 0;

        for (Clip clip : data.camera.get())
        {
            layer = Math.max(layer, clip.layer.get());
        }

        for (UIScreenplayAction uiAction : this.editor.getChildren(UIScreenplayAction.class))
        {
            Wave wave = uiAction.audioPlayer.getWave();

            if (wave == null)
            {
                continue;
            }

            ScreenplayAction action = uiAction.getAction();
            float pause = action.pause.get();
            float time = offset + (pause < 0 ? -pause : 0);
            float duration = wave.getDuration();

            SubtitleClip clip = new SubtitleClip();

            clip.title.set(action.content.get());
            clip.tick.set((int) (time * 20));
            clip.duration.set((int) (duration * 20));
            clip.layer.set(layer + 1);
            clip.color.set(BBSSettings.elevenVoiceColors.getColor(action.voice.get()));

            data.camera.addClip(clip);

            offset += pause + duration;
        }

        this.panel.showPanel(this.panel.clips);
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

            UIOverlay.addOverlay(this.getContext(), new UIMessageFolderOverlayPanel(
                IKey.lazy("Compiled"),
                IKey.lazy("Generated voice lines were successfully generated to " + filename + "!"),
                folder
            ));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public File getSoundsFolder()
    {
        return BBS.getAssetsPath("audio/elevenlabs/" + this.panel.getData().getId());
    }

    public void generateTTS(List<ScreenplayAction> actions)
    {
        this.generateTTS(actions, null);
    }

    public void generateTTS(List<ScreenplayAction> actions, Consumer<List<ScreenplayAction>> callback)
    {
        try
        {
            ElevenLabsAPI.generate(this.getSoundsFolder(), actions, (result) ->
            {
                if (result.status == ElevenLabsResult.Status.INITIALIZED)
                {
                    this.getContext().notify(IKey.lazy("Starting TTS generation!"), Colors.BLUE | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.GENERATED)
                {
                    this.getContext().notify(IKey.lazy(result.message), Colors.BLUE | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.ERROR)
                {
                    this.getContext().notify(IKey.lazy("An error has occurred when generating a voice line: " + result.message), Colors.RED | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.TOKEN_MISSING)
                {
                    this.getContext().notify(IKey.lazy("You haven't specified a token in BBS' settings!"), Colors.RED | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.VOICE_IS_MISSING)
                {
                    this.getContext().notify(!result.missingVoices.isEmpty()
                        ? IKey.lazy("Following voices in the screenplay are missing: " + String.join(", ", result.missingVoices))
                        : IKey.lazy("A list of voices couldn't get loaded!"),
                Colors.RED | Colors.A100);
                }
                else /* SUCCESS */
                {
                    if (callback != null)
                    {
                        callback.accept(actions);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private UIScreenplayAction createAction(ScreenplayAction action)
    {
        UIScreenplayAction uiAction = new UIScreenplayAction(this, action);

        uiAction.context((menu) ->
        {
            menu.action(Icons.REMOVE, IKey.lazy("Remove action"), Colors.NEGATIVE, () ->
            {
                this.screenplay.removeAction(action);
                uiAction.removeFromParent();
                this.editor.resize();
            });
        });

        return uiAction;
    }

    public void setScreenplay(Screenplay screenplay)
    {
        this.screenplay = screenplay;

        this.fillData();

        for (UIScreenplayAction action : this.getChildren(UIScreenplayAction.class))
        {
            action.load();
        }
    }

    private void fillData()
    {
        this.editor.removeAll();

        for (ScreenplayAction action : this.screenplay.getList())
        {
            this.editor.add(this.createAction(action));
        }

        this.editor.add(this.add);
        this.resize();
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, this.style.background | Colors.A100);

        super.render(context);
    }
}