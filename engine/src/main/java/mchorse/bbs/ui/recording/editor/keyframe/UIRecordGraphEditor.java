package mchorse.bbs.ui.recording.editor.keyframe;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.recording.editor.IUIRecordEditorDelegate;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

public class UIRecordGraphEditor extends UIKeyframesEditor<UIRecordGraph>
{
    public UIRecordGraphEditor(IUIRecordEditorDelegate delegate)
    {
        super();

        this.keyframes.delegate = delegate;
    }

    @Override
    protected UIRecordGraph createElement()
    {
        return new UIRecordGraph(this::fillData);
    }

    public void setChannel(KeyframeChannel channel, int color)
    {
        this.keyframes.clearSelection();
        this.keyframes.setChannel(channel, color);
        this.keyframes.resetView();
        this.frameButtons.setVisible(false);
    }
}