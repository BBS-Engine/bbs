package mchorse.bbs.ui.film.utils.keyframes;

import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.UIClips;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframes;
import mchorse.bbs.utils.keyframes.Keyframe;

import java.util.function.Consumer;

public class UIDopeSheetView extends UIKeyframes
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
    protected void moveNoKeyframe(UIContext context, double x, double y)
    {
        if (this.editor != null)
        {
            long offset = this.getClipOffset();

            this.editor.setCursor((int) (x + offset));
        }
    }

    @Override
    protected void renderCursor(UIContext context)
    {
        if (this.editor != null)
        {
            int cx = this.toGraphX(this.getOffset());
            String label = TimeUtils.formatTime(this.getOffset()) + "/" + TimeUtils.formatTime(this.duration);

            UIClips.renderCursor(context, label, this.area, cx - 1);
        }
    }
}