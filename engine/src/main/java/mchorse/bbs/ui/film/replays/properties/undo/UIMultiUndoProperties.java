package mchorse.bbs.ui.film.replays.properties.undo;

import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.replays.properties.UIMultiProperties;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class UIMultiUndoProperties extends UIMultiProperties
{
    protected UIUndoPropertyEditor keyframeEditor;

    public UIMultiUndoProperties(IUIClipsDelegate delegate, UIUndoPropertyEditor editor)
    {
        super(delegate, editor::fillData);

        this.keyframeEditor = editor;
    }

    @Override
    protected void pickedKeyframe(int amount)
    {
        super.pickedKeyframe(amount);

        if (amount > 0)
        {
            this.keyframeEditor.markUndo(100);
        }
    }

    @Override
    protected void keepMoving()
    {
        super.keepMoving();
        this.keyframeEditor.markUndo(100);
    }

    @Override
    protected void resetMouseReleased(UIContext context)
    {
        if (this.keyframeEditor.getUndo() == 100)
        {
            GenericKeyframe keyframe = this.getCurrent();

            if (!this.moving || !this.selected || keyframe.tick == this.lastT)
            {
                this.keyframeEditor.cancelUndo();
            }
        }

        super.resetMouseReleased(context);
    }
}