package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIGraph;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIGraphView extends UIGraph
{
    public UIAnimationPanel editor;

    private UIGraphEditor keyframeEditor;

    public UIGraphView(UIGraphEditor keyframeEditor, Consumer<Keyframe> callback)
    {
        super(callback);

        this.keyframeEditor = keyframeEditor;
    }

    public int getOffset()
    {
        if (this.editor == null)
        {
            return 0;
        }

        return this.editor.getTick();
    }

    @Override
    protected void moveNoKeyframe(UIContext context, Keyframe frame, double x, double y)
    {
        if (this.editor != null)
        {
            this.editor.setTickFill((int) x);
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