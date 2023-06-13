package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.RemapperClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.utils.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.utils.colors.Colors;

public class UIRemapperClip extends UIClip<RemapperClip>
{
    public UICameraGraphEditor channel;
    public UIButton editChannel;

    public UIRemapperClip(RemapperClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.channel = new UICameraGraphEditor(editor);

        this.editChannel = new UIButton(UIKeys.CAMERA_PANELS_EDIT_KEYFRAMES, (b) ->
        {
            this.editor.timeline.embedView(this.channel);
            this.channel.resetView();
        });

        this.left.addAfter(this.duration, this.editChannel);
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