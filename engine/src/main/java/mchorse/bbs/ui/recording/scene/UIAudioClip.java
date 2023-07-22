package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.data.types.StringType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.scene.AudioClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;

public class UIAudioClip extends UIClip<AudioClip>
{
    public UIButton pickAudio;

    public UIAudioClip(AudioClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.pickAudio = new UIButton(UIKeys.CAMERA_PANELS_AUDIO_PICK_AUDIO, (b) ->
        {
            UISoundOverlayPanel panel = new UISoundOverlayPanel((l) ->
            {
                editor.postUndo(this.undo(this.clip.audio, l == null ? null : new StringType(l.toString())));
            });

            UIOverlay.addOverlay(this.getContext(), panel.set(this.clip.audio.get()));
        });
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(IKey.lazy("Audio")).marginTop(12), this.pickAudio);
    }
}