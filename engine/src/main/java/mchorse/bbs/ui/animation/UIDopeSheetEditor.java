package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;

import java.util.List;

public class UIDopeSheetEditor extends UIKeyframesEditor<UIDopeSheet>
{
    public UIDopeSheetEditor(UIAnimationPanel editor)
    {
        super();

        this.keyframes.editor = editor;
    }

    @Override
    protected UIDopeSheet createElement()
    {
        return new UIDopeSheet(this, this::fillData);
    }

    public void setSheets(List<UISheet> sheets)
    {
        this.keyframes.sheets.clear();
        this.keyframes.sheets.addAll(sheets);
    }
}