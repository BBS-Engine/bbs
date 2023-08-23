package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.RemapperClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.utils.keyframes.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.utils.colors.Colors;

public class UIRemapperClip extends UIClip<RemapperClip>
{
    public UICameraGraphEditor channel;
    public UIButton editChannel;

    public UIRemapperClip(RemapperClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.channel = new UICameraGraphEditor(editor);

        this.editChannel = new UIButton(UIKeys.CAMERA_PANELS_EDIT_KEYFRAMES, (b) ->
        {
            this.editor.embedView(this.channel);
            this.channel.resetView();
        });
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(IKey.lazy("Remapper")).marginTop(12), this.editChannel);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.channel.keyframes.setDuration(this.clip.duration.get());
        this.channel.setChannel(this.clip.channel, Colors.ACTIVE);
    }

    @Override
    public void updateDuration(int duration)
    {
        super.updateDuration(duration);

        this.channel.keyframes.duration = duration;
    }
}