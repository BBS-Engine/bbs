package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;

import java.util.List;

public class UIAnimationDopeSheetEditor extends UIKeyframesEditor<UIAnimationDopeSheet>
{
    public UIAnimationDopeSheetEditor(UIAnimationPanel editor)
    {
        super();

        this.keyframes.editor = editor;
    }

    @Override
    protected UIAnimationDopeSheet createElement()
    {
        return new UIAnimationDopeSheet(this, this::fillData);
    }

    public void setSheets(List<UISheet> sheets)
    {
        this.keyframes.sheets.clear();
        this.keyframes.sheets.addAll(sheets);
    }
}