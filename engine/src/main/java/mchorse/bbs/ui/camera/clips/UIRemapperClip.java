package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.RemapperClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.utils.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIRemapperClip extends UIClip<RemapperClip>
{
    public UICameraGraphEditor channel;
    public UIButton editChannel;

    public UIRemapperClip(RemapperClip clip, UICameraPanel editor)
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
            this.editor.timeline.embedView(this.channel);
            this.channel.resetView();
        });
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView remapper = this.createScroll();

        remapper.add(this.editChannel);

        this.panels.registerPanel(remapper, UIKeys.CAMERA_PANELS_REMAPPER, Icons.TIME);
        this.panels.setPanel(remapper);

        super.registerPanels();
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