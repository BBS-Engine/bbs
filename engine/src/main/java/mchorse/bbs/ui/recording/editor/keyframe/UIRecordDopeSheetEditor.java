package mchorse.bbs.ui.recording.editor.keyframe;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.recording.editor.IUIRecordEditorDelegate;

public class UIRecordDopeSheetEditor extends UIKeyframesEditor<UIRecordDopeSheet>
{
    public UIRecordDopeSheetEditor(IUIRecordEditorDelegate delegate)
    {
        super();

        this.keyframes.delegate = delegate;
    }

    @Override
    protected UIRecordDopeSheet createElement()
    {
        return new UIRecordDopeSheet(this::fillData);
    }
}