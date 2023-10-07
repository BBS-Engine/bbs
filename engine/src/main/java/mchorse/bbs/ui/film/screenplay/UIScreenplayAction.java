package mchorse.bbs.ui.film.screenplay;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.film.screenplay.ScreenplayAction;
import mchorse.bbs.film.tts.ElevenLabsAPI;
import mchorse.bbs.film.tts.ElevenLabsVoice;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.tooltips.ITooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.io.File;
import java.util.Arrays;

public class UIScreenplayAction extends UIElement
{
    public UITextbox content;
    public UIIcon pickVoice;
    public UITrackpad pause;
    public UITrackpad cutoff;
    public UIAudioPlayer audioPlayer;

    private ScreenplayAction action;
    private UIScreenplayEditor editor;

    public UIScreenplayAction(UIScreenplayEditor editor, ScreenplayAction action)
    {
        this.editor = editor;
        this.action = action;

        this.content = new UITextbox(10000, (t) -> this.action.content.set(t));
        this.pickVoice = new UIIcon(Icons.VOICE, (b) -> this.pickVoice());

        this.pause = new UITrackpad((v) -> this.action.pause.set(v.floatValue()));
        this.pause.tooltip(IKey.lazy("Pause (silence)")).w(60);
        this.cutoff = new UITrackpad((v) ->
        {
            this.action.cutoff.set(v.floatValue());
            this.load();
        });
        this.cutoff.delayedInput().tooltip(IKey.lazy("Cutoff")).w(60);
        this.audioPlayer = new UIAudioPlayer();
        this.audioPlayer.context((menu) ->
        {
            menu.action(Icons.SOUND, IKey.lazy("Generate voice line (ElevenLabs)"), () ->
            {
                this.editor.generateTTS(Arrays.asList(this.action), (actions) ->
                {
                    this.getContext().render.postRunnable(this::load);
                });
            });

            File folder = new File(this.editor.getSoundsFolder(), this.action.uuid.get());

            if (!folder.exists())
            {
                return;
            }

            menu.action(Icons.FOLDER, IKey.lazy("Open folder..."), () -> UIUtils.openFolder(folder));
            menu.action(Icons.POINTER, IKey.lazy("Pick a variant..."), () ->
            {
                this.getContext().replaceContextMenu((m) ->
                {
                    for (File file : folder.listFiles())
                    {
                        if (!file.getName().endsWith(".wav"))
                        {
                            continue;
                        }

                        m.action(Icons.SOUND, IKey.raw(file.getName()), () ->
                        {
                            this.action.variant.set(file.getName());
                            this.load();
                        });
                    }
                });
            });
        });

        this.fillData();
        this.rebuild();
        this.column(5).stretch().vertical();
    }

    private void pickVoice()
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            for (ElevenLabsVoice voice : ElevenLabsAPI.getVoices().values())
            {
                if (!voice.isCloned())
                {
                    continue;
                }

                String name = voice.name;

                if (name.equalsIgnoreCase(this.action.voice.get()))
                {
                    menu.action(Icons.VOICE, IKey.raw(name), BBSSettings.primaryColor(0), () -> this.setVoice(name));
                }
                else
                {
                    menu.action(Icons.VOICE, IKey.raw(name), () -> this.setVoice(name));
                }
            }

            menu.action(Icons.CLOSE, IKey.lazy("No voice"), Colors.NEGATIVE, () -> this.setVoice(""));
        });
    }

    public ScreenplayAction getAction()
    {
        return this.action;
    }

    private void setVoice(String voice)
    {
        this.action.voice.set(voice);

        this.rebuild();
        this.updateVoiceTooltip();
    }

    public void fillData()
    {
        this.content.setText(this.action.content.get());
        this.pause.setValue(this.action.pause.get());
        this.cutoff.setValue(this.action.cutoff.get());

        this.updateVoiceTooltip();
        this.updateVariantTooltip();
    }

    private void updateVariantTooltip()
    {
        String variant = this.action.variant.get();

        if (variant.isEmpty())
        {
            this.audioPlayer.tooltip((ITooltip) null);
        }
        else
        {
            this.audioPlayer.tooltip(IKey.raw(variant));
        }
    }

    private void updateVoiceTooltip()
    {
        String voice = this.action.voice.get();

        if (voice.isEmpty())
        {
            this.pickVoice.tooltip((ITooltip) null);
        }
        else
        {
            this.pickVoice.tooltip(IKey.raw(voice));
        }
    }

    public void rebuild()
    {
        this.removeAll();

        if (this.action.voice.get().isEmpty())
        {
            this.add(UI.row(this.content, this.pickVoice));
        }
        else
        {
            this.add(UI.row(this.audioPlayer, this.pickVoice, this.pause, this.cutoff), this.content);
        }

        UIElement container = this.getParentContainer();

        if (container != null)
        {
            container.resize();
        }
    }

    public void load()
    {
        File soundsFolder = this.editor.getSoundsFolder();
        File file = new File(soundsFolder, this.action.uuid.get() + "/" + this.action.variant.get());

        this.audioPlayer.loadAudio(file, this.action.cutoff.get());
        this.updateVariantTooltip();
    }
}