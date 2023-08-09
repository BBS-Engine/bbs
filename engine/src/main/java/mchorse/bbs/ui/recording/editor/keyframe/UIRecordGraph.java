package mchorse.bbs.ui.recording.editor.keyframe;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIGraph;
import mchorse.bbs.ui.recording.editor.IUIRecordEditorDelegate;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIRecordGraph extends UIGraph
{
    public IUIRecordEditorDelegate delegate;

    public UIRecordGraph(Consumer<Keyframe> callback)
    {
        super(callback);
    }

    public int getOffset()
    {
        if (this.delegate == null)
        {
            return 0;
        }

        return this.delegate.getCursor();
    }

    @Override
    protected void moveNoKeyframe(UIContext context, Keyframe frame, double x, double y)
    {
        if (this.delegate != null && this.delegate.supportsCursor())
        {
            this.delegate.setCursor((int) x);
        }
    }

    @Override
    protected void renderCursor(UIContext context)
    {
        if (this.delegate != null && this.delegate.supportsCursor())
        {
            int cx = this.toGraphX(this.getOffset());

            context.batcher.box(cx - 1, this.area.y, cx + 1, this.area.ey(), Colors.CURSOR);
        }
    }
}