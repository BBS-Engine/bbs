package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.data.types.StringType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.scene.AudioClip;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

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
        UIScrollView audio = this.createScroll();

        audio.add(this.pickAudio);

        this.panels.registerPanel(audio, UIKeys.C_CLIP.get(Link.bbs("audio")), Icons.SOUND);
        this.panels.setPanel(audio);

        super.registerPanels();
    }
}