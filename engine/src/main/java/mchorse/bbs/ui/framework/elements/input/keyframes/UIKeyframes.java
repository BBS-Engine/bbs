package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Scale;
import mchorse.bbs.utils.OS;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.List;
import java.util.function.Consumer;

public abstract class UIKeyframes extends UIElement
{
    public static final Color COLOR = new Color();

    public Consumer<Keyframe> callback;
    public Selection which = Selection.NOT_SELECTED;
    public int duration;

    public static final double MIN_ZOOM = 0.01D;
    public static final double MAX_ZOOM = 1000D;

    /**
     * Sliding flag, whether keyframes should be sorted after
     * dragging keyframes around
     */
    public boolean sliding;

    /**
     * Dragging flag, whether dragging got initiated (it might be possible
     * that there are 0 keyframes selected)
     */
    public boolean dragging;

    /**
     * Moving flag, whether the user dragged 3 pixels away from the original
     * place (also could have 0 keyframes selected)
     */
    protected boolean moving;

    /**
     * Scrolling flag, whether the user was navigating by dragging with
     * middle mouse held
     */
    protected boolean scrolling;

    /**
     * Grabbing flag, whether the user selected an area with Shift + click dragging
     * in order to select multiple keyframes
     */
    protected boolean grabbing;

    protected int lastX;
    protected int lastY;
    protected double lastT;
    protected double lastV;

    protected Scale scaleX;

    protected IAxisConverter converter;

    public UIKeyframes(Consumer<Keyframe> callback)
    {
        super();

        this.callback = callback;
        this.scaleX = new Scale(this.area);
        this.scaleX.anchor(0.5F);
    }

    public void setConverter(IAxisConverter converter)
    {
        this.converter = converter;
    }

    public Scale getScaleX()
    {
        return this.scaleX;
    }

    protected void setKeyframe(Keyframe current)
    {
        if (this.callback != null)
        {
            this.callback.accept(current);
        }
    }

    /* Setters */

    public abstract void setTick(double tick, boolean opposite);

    public abstract void setValue(double value, boolean opposite);

    public abstract void setInterpolation(KeyframeInterpolation interp);

    public abstract void setEasing(KeyframeEasing easing);

    public void setDuration(long duration)
    {
        this.duration = (int) duration;
    }

    /* Graphing code */

    public abstract void resetView();

    public int toGraphX(double tick)
    {
        return (int) this.scaleX.to(tick);
    }

    public double fromGraphX(int mouseX)
    {
        return this.scaleX.from(mouseX);
    }

    /* Abstract methods */

    public abstract Keyframe getCurrent();

    public abstract List<UISheet> getSheets();

    public abstract UISheet getSheet(int mouseY);

    public boolean isGrabbing()
    {
        return this.dragging && this.moving && this.grabbing;
    }

    public void selectByDuration(long duration)
    {}

    public abstract void selectAll();

    public abstract int getSelectedCount();

    public boolean isMultipleSelected()
    {
        return this.getSelectedCount() > 1;
    }

    public boolean hasSelected()
    {
        return this.getSelectedCount() > 0;
    }

    public abstract void clearSelection();

    public void doubleClick(int mouseX, int mouseY)
    {
        if (this.which == Selection.NOT_SELECTED)
        {
            this.addCurrent(mouseX, mouseY);
        }
        else if (this.which == Selection.KEYFRAME && !this.isMultipleSelected())
        {
            this.removeCurrent();
        }
    }

    public abstract void addCurrent(int mouseX, int mouseY);

    public abstract void removeCurrent();

    public abstract void removeSelectedKeyframes();

    /* Common hooks */

    protected void moveNoKeyframe(UIContext context, Keyframe frame, double x, double y)
    {}

    protected void renderCursor(UIContext context)
    {}

    /* Mouse input handling */

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        /* Select current point with a mouse click */
        if (this.area.isInside(mouseX, mouseY))
        {
            if (context.mouseButton == 0)
            {
                boolean shift = Window.isShiftPressed();

                /* Duplicate the keyframe */
                if (Window.isAltPressed() && !shift && this.which == Selection.KEYFRAME)
                {
                    this.duplicateKeyframe(context, mouseX, mouseY);

                    return false;
                }

                this.lastX = mouseX;
                this.lastY = mouseY;

                if (shift)
                {
                    this.grabbing = true;
                }

                if (!this.pickKeyframe(context, mouseX, mouseY, shift) && !shift)
                {
                    this.clearSelection();
                    this.setKeyframe(null);
                }

                this.dragging = true;
            }
            else if (context.mouseButton == 2)
            {
                this.setupScrolling(context, mouseX, mouseY);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    protected abstract void duplicateKeyframe(UIContext context, int mouseX, int mouseY);

    protected abstract boolean pickKeyframe(UIContext context, int mouseX, int mouseY, boolean multi);

    protected void setupScrolling(UIContext context, int mouseX, int mouseY)
    {
        this.scrolling = true;
        this.lastX = mouseX;
        this.lastY = mouseY;
        this.lastT = this.scaleX.getShift();
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        if (this.area.isInside(context.mouseX, context.mouseY) && !this.scrolling)
        {
            int scroll = context.mouseWheel;

            if (OS.CURRENT != OS.MACOS)
            {
                scroll = -scroll;
            }

            this.zoom(context, scroll);

            return true;
        }

        return super.subMouseScrolled(context);
    }

    protected void zoom(UIContext context, int scroll)
    {
        this.scaleX.zoomAnchor(Scale.getAnchorX(context, this.area), Math.copySign(this.scaleX.getZoomFactor(), scroll), MIN_ZOOM, MAX_ZOOM);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.which == Selection.KEYFRAME)
        {
            if (this.sliding)
            {
                /* Resort after dragging the tick thing */
                for (UISheet sheet : this.getSheets())
                {
                    if (!sheet.selected.isEmpty())
                    {
                        sheet.sort();
                    }
                }

                this.sliding = false;
            }
        }

        this.resetMouseReleased(context);

        return super.subMouseReleased(context);
    }

    protected void resetMouseReleased(UIContext context)
    {
        this.grabbing = false;
        this.dragging = false;
        this.moving = false;
        this.scrolling = false;
    }

    /* Rendering */

    @Override
    public void render(UIContext context)
    {
        this.handleMouse(context, context.mouseX, context.mouseY);
        this.renderBackground(context);

        context.batcher.clip(this.area, context);

        this.renderGrid(context);
        this.renderCursor(context);

        /* Draw graph of the keyframe channel */
        this.renderGraph(context);

        /* Draw selection box */
        if (this.isGrabbing())
        {
            context.batcher.normalizedBox(this.lastX, this.lastY, context.mouseX, context.mouseY, Colors.setA(Colors.ACTIVE, 0.25F));
        }

        context.batcher.unclip(context);

        super.render(context);
    }

    protected void renderBackground(UIContext context)
    {
        this.area.render(context.batcher, Colors.A50);

        if (this.duration > 0)
        {
            int leftBorder = this.toGraphX(0);
            int rightBorder = this.toGraphX(this.duration);

            if (leftBorder > this.area.x) context.batcher.box(this.area.x, this.area.y, leftBorder, this.area.y + this.area.h, Colors.A50);
            if (rightBorder < this.area.ex()) context.batcher.box(rightBorder, this.area.y, this.area.ex() , this.area.y + this.area.h, Colors.A50);
        }
    }

    protected void renderGrid(UIContext context)
    {
        /* Draw scaling grid */
        int mult = this.scaleX.getMult();
        int hx = this.duration / mult;
        int ht = (int) this.fromGraphX(this.area.x);

        for (int j = Math.max(ht / mult, 0); j <= hx; j++)
        {
            int x = this.toGraphX(j * mult);

            if (x >= this.area.ex())
            {
                break;
            }

            String label = this.converter == null ? String.valueOf(j * mult) : this.converter.format(j * mult);

            context.batcher.box(x, this.area.y, x + 1, this.area.ey(), Colors.setA(Colors.WHITE, 0.25F));
            context.batcher.text(label, x + 4, this.area.y + 4);
        }
    }

    protected abstract void renderGraph(UIContext context);

    protected void renderRect(UIContext context, int x, int y, int offset, int c)
    {
        c = Colors.A100 | c;

        context.batcher.box(x - offset, y - offset, x + offset, y + offset, c);
    }

    /* Handling dragging */

    protected void handleMouse(UIContext context, int mouseX, int mouseY)
    {
        if (this.dragging && !this.moving && (Math.abs(this.lastX - mouseX) > 3 || Math.abs(this.lastY - mouseY) > 3))
        {
            this.moving = true;
            this.sliding = true;
        }

        if (this.scrolling)
        {
            this.scrolling(mouseX, mouseY);
        }
        /* Move the current keyframe */
        else if (this.moving && !this.grabbing)
        {
            this.setKeyframe(this.moving(context, mouseX, mouseY));
        }
    }

    protected void scrolling(int mouseX, int mouseY)
    {
        this.scaleX.setShift(-(mouseX - this.lastX) / this.scaleX.getZoom() + this.lastT);
    }

    protected Keyframe moving(UIContext context, int mouseX, int mouseY)
    {
        return null;
    }
}