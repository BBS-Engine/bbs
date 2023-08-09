package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.Selection;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIGraph;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIGraphView extends UIGraph
{
    public IUIClipsDelegate editor;

    private UICameraGraphEditor keyframeEditor;

    public UIGraphView(UICameraGraphEditor keyframeEditor, Consumer<Keyframe> callback)
    {
        super(callback);

        this.keyframeEditor = keyframeEditor;
    }

    public long getFixtureOffset()
    {
        if (this.editor == null || this.editor.getClip() == null)
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

        return (int) (this.editor.getCursor() - this.getFixtureOffset());
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
            Keyframe keyframe = this.getCurrent();

            if (!this.moving || this.which == Selection.NOT_SELECTED || (this.which.getX(keyframe) == this.lastT && this.which.getY(keyframe) == this.lastV))
            {
                this.keyframeEditor.cancelUndo();
            }
        }

        super.resetMouseReleased(context);
    }

    @Override
    protected void moveNoKeyframe(UIContext context, Keyframe frame, double x, double y)
    {
        if (this.editor != null)
        {
            long offset = this.getFixtureOffset();

            this.editor.setCursor((int) (x + offset));
        }
    }

    @Override
    protected void renderCursor(UIContext context)
    {
        if (this.editor != null)
        {
            int cx = this.getOffset();
            int cy = this.toGraphY(this.sheet.channel.interpolate(cx));

            cx = this.toGraphX(cx);

            if (cy < this.area.ey() && cx >= this.area.x && cx <= this.area.ex())
            {
                context.batcher.box(cx - 1, cy, cx + 1, this.area.ey(), Colors.CURSOR);
            }
        }
    }
}