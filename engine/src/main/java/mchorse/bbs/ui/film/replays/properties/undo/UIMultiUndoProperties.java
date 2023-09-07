package mchorse.bbs.ui.film.replays.properties.undo;

import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.replays.properties.UIMultiProperties;

public class UIMultiUndoProperties extends UIMultiProperties
{
    protected UIUndoPropertyEditor keyframeEditor;

    public UIMultiUndoProperties(IUIClipsDelegate delegate, UIUndoPropertyEditor editor)
    {
        super(delegate, editor::fillData);

        this.keyframeEditor = editor;
    }
}