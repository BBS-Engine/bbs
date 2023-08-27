package mchorse.bbs.ui.film.utils.keyframes;

import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.Selection;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIDopeSheet;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIDopeSheetView extends UIDopeSheet
{
    public IUIClipsDelegate editor;

    private UICameraDopeSheetEditor keyframeEditor;
    private boolean relative = true;

    public UIDopeSheetView(UICameraDopeSheetEditor keyframeEditor, Consumer<Keyframe> callback)
    {
        super(callback);

        this.keyframeEditor = keyframeEditor;
    }

    public UIDopeSheetView absolute()
    {
        this.relative = false;

        return this;
    }

    public long getClipOffset()
    {
        if (this.editor == null || this.editor.getClip() == null || !this.relative)
        {
            return 0;
        }

        return this.editor.getClip().tick.get();
    }

    public int getOffset()
    {
        if (this.editor == null)
        {
            return 0;
        }

        return (int) (this.editor.getCursor() - this.getClipOffset());
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
    protected void moveNoKeyframe(UIContext context, Keyframe frame, double x, double y)
    {
        if (this.editor != null)
        {
            long offset = this.getClipOffset();

            this.editor.setCursor((int) (x + offset));
        }
    }

    @Override
    protected void resetMouseReleased(UIContext context)
    {
        if (this.keyframeEditor.getUndo() == 100)
        {
            Keyframe keyframe = this.getCurrent();

            if (!this.moving || this.which == Selection.NOT_SELECTED || this.which.getX(keyframe) == this.lastT)
            {
                this.keyframeEditor.cancelUndo();
            }
        }

        super.resetMouseReleased(context);
    }

    @Override
    protected void renderCursor(UIContext context)
    {
        if (this.editor != null)
        {
            int cx = this.toGraphX(this.getOffset());

            context.batcher.box(cx - 1, this.area.y, cx + 1, this.area.ey(), Colors.CURSOR);
        }
    }
}