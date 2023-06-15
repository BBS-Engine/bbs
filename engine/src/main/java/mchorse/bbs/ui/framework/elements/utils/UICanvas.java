package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Scale;
import mchorse.bbs.ui.utils.ScrollDirection;

public abstract class UICanvas extends UIElement
{
    public Scale scaleX;
    public Scale scaleY;

    public boolean dragging;
    public int mouse;

    protected int lastX;
    protected int lastY;
    protected double lastT;
    protected double lastV;

    public UICanvas()
    {
        super();

        this.scaleX = new Scale(this.area, false);
        this.scaleX.anchor(0.5F);
        this.scaleY = new Scale(this.area, ScrollDirection.VERTICAL, false);
        this.scaleY.anchor(0.5F);
    }

    public int toX(double x)
    {
        return (int) Math.round(this.scaleX.to(x));
    }

    public double fromX(int mouseX)
    {
        return this.scaleX.from(mouseX);
    }

    public int toY(double y)
    {
        return (int) Math.round(this.scaleY.to(y));
    }

    public double fromY(int mouseY)
    {
        return this.scaleY.from(mouseY);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && this.isMouseButtonAllowed(context.mouseButton))
        {
            this.dragging = true;
            this.mouse = context.mouseButton;

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            /* Fake middle mouse click to add an ability to navigate
             * with Ctrl + click dragging */
            if (this.mouse == 0 && Window.isCtrlPressed())
            {
                this.mouse = 2;
            }

            this.startDragging(context);

            return true;
        }

        return super.subMouseClicked(context);
    }

    protected boolean isMouseButtonAllowed(int mouseButton)
    {
        return mouseButton == 0 || mouseButton == 2;
    }

    protected void startDragging(UIContext context)
    {
        this.lastT = this.scaleX.getShift();
        this.lastV = this.scaleY.getShift();
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        if (this.area.isInside(context.mouseX, context.mouseY) && !this.dragging)
        {
            this.zoom(context.mouseWheel);
        }

        return super.subMouseScrolled(context);
    }

    protected void zoom(int scroll)
    {
        this.scaleX.zoom(Math.copySign(this.scaleX.getZoomFactor(), scroll), 0.001, 1000);
        this.scaleY.zoom(Math.copySign(this.scaleY.getZoomFactor(), scroll), 0.001, 1000);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.dragging = false;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.dragging(context);

        context.batcher.clip(this.area, context);
        this.renderCanvas(context);
        context.batcher.unclip(context);

        super.render(context);
    }

    protected void dragging(UIContext context)
    {
        if (this.dragging && this.mouse == 2)
        {
            float y = this.scaleY.inverse ? 1 : -1;

            this.scaleX.setShift(-(context.mouseX - this.lastX) / this.scaleX.getZoom() + this.lastT);
            this.scaleY.setShift(y * (context.mouseY - this.lastY) / this.scaleY.getZoom() + this.lastV);
        }
    }

    protected void renderCanvas(UIContext context)
    {}
}