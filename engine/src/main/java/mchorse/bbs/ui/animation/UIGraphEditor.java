package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

public class UIGraphEditor extends UIKeyframesEditor<UIGraphView>
{
    public UIGraphEditor(UIAnimationPanel editor)
    {
        super();

        this.keyframes.editor = editor;
    }

    @Override
    protected UIGraphView createElement()
    {
        return new UIGraphView(this, this::fillData);
    }

    public void setChannel(KeyframeChannel channel, int color)
    {
        this.keyframes.clearSelection();
        this.keyframes.setChannel(channel, color);
        this.keyframes.resetView();
        this.frameButtons.setVisible(false);
    }
}