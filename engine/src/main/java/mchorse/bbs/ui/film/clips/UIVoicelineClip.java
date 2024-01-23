package mchorse.bbs.ui.film.clips;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.clips.misc.VoicelineClip;
import mchorse.bbs.film.tts.ElevenLabsAPI;
import mchorse.bbs.film.tts.ElevenLabsResult;
import mchorse.bbs.film.tts.ElevenLabsVoice;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UIVoicelineClip extends UIClip<VoicelineClip>
{
    public UITextbox content;
    public UIIcon generate;
    public UIIcon voice;
    public UIIcon variant;
    public UIIcon folder;
    public UIIcon uuid;

    public UIVoicelineClip(VoicelineClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.content = new UITextbox(10000, (t) -> this.clip.content.set(t));
        this.generate = new UIIcon(Icons.REFRESH, (b) -> this.generate());
        this.voice = new UIIcon(Icons.VOICE, (b) -> this.pickVoice());
        this.variant = new UIIcon(Icons.SOUND, (b) -> this.pickVariant());
        this.folder = new UIIcon(Icons.FOLDER, (b) -> UIUtils.openFolder(this.getFolder()));
        this.uuid = new UIIcon(Icons.EDIT, (b) ->
        {
            UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
                UIKeys.CAMERA_PANELS_VOICE_UUID_TITLE,
                UIKeys.CAMERA_PANELS_VOICE_UUID_DESCRIPTION,
                (s) -> this.clip.uuid.set(s)
            );

            panel.text.setText(this.clip.uuid.get());

            UIOverlay.addOverlay(this.getContext(), panel);
        });

        this.generate.w(0).tooltip(UIKeys.CAMERA_PANELS_VOICE_LINE_GENERATE);
        this.voice.w(0).tooltip(UIKeys.CAMERA_PANELS_VOICE_LINE_VOICE);
        this.variant.w(0).tooltip(UIKeys.CAMERA_PANELS_VOICE_LINE_VARIANT);
        this.folder.w(0).tooltip(UIKeys.CAMERA_PANELS_VOICE_LINE_FOLDER);
        this.uuid.w(0).tooltip(UIKeys.CAMERA_PANELS_VOICE_UUID);
    }

    private void generate()
    {
        this.generateTTS(Collections.singletonList(this.clip), null);
    }

    public void generateTTS(List<VoicelineClip> actions, Consumer<List<VoicelineClip>> callback)
    {
        ElevenLabsAPI.generateStandard(this.getContext(), UIFilmPanel.getVoiceLines().getFolder(), actions, (result) ->
        {
            if (callback != null && result.status == ElevenLabsResult.Status.SUCCESS)
            {
                callback.accept(actions);
            }
        });
    }

    private void pickVoice()
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            for (ElevenLabsVoice voice : ElevenLabsAPI.getVoices().values())
            {
                if (!voice.isAllowed())
                {
                    continue;
                }

                String name = voice.name;
                int color = BBSSettings.elevenVoiceColors.getColor(name) & 0xffffff;

                if (name.equalsIgnoreCase(this.clip.voice.get()))
                {
                    color = BBSSettings.primaryColor(0);
                }

                if (color != 0xffffff)
                {
                    menu.action(Icons.VOICE, IKey.raw(name), color, () -> this.setVoice(name));
                }
                else
                {
                    menu.action(Icons.VOICE, IKey.raw(name), () -> this.setVoice(name));
                }
            }

            menu.action(Icons.CLOSE, UIKeys.VOICE_LINE_ACTION_NO_VOICE, Colors.NEGATIVE, () -> this.setVoice(""));
        });
    }

    private void setVoice(String voice)
    {
        this.clip.voice.set(voice);
    }

    private void pickVariant()
    {
        File folder = this.getFolder();

        if (!folder.exists())
        {
            return;
        }

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
                    this.clip.variant.set(file.getName());
                });
            }
        });
    }

    private File getFolder()
    {
        return new File(UIFilmPanel.getVoiceLines().getFolder(), this.clip.uuid.get());
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.C_CLIP.get("bbs:voice_line")).marginTop(12), this.content);
        this.panels.add(UI.row(this.generate, this.voice, this.variant, this.folder, this.uuid));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.content.setText(this.clip.content.get());
    }
}