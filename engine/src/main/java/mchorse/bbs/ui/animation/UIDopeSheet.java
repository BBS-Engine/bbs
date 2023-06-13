package mchorse.bbs.ui.animation;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIDopeSheet extends mchorse.bbs.ui.framework.elements.input.keyframes.UIDopeSheet
{
    public UIAnimationPanel editor;

    private UIDopeSheetEditor keyframeEditor;

    public UIDopeSheet(UIDopeSheetEditor keyframeEditor, Consumer<Keyframe> callback)
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
            int cx = this.toGraphX(this.getOffset());

            context.draw.box(cx - 1, this.area.y, cx + 1, this.area.ey(), Colors.CURSOR);
        }
    }
}