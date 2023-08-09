package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

public class UIAnimationGraphEditor extends UIKeyframesEditor<UIAnimationGraph>
{
    public UIAnimationGraphEditor(UIAnimationPanel editor)
    {
        super();

        this.keyframes.editor = editor;
    }

    @Override
    protected UIAnimationGraph createElement()
    {
        return new UIAnimationGraph(this, this::fillData);
    }

    public void setChannel(KeyframeChannel channel, int color)
    {
        this.keyframes.clearSelection();
        this.keyframes.setChannel(channel, color);
        this.keyframes.resetView();
        this.frameButtons.setVisible(false);
    }
}