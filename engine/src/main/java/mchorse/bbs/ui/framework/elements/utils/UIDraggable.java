package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UIDraggable extends UIElement
{
    private Consumer<UIContext> callback;
    private boolean dragging;
    private boolean hover;

    public UIDraggable(Consumer<UIContext> callback)
    {
        this.callback = callback;
    }

    public UIDraggable hoverOnly()
    {
        this.hover = true;

        return this;
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            this.dragging = true;

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected boolean subMouseReleased(UIContext context)
    {
        this.dragging = false;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (!this.hover || this.area.isInside(context) || this.dragging)
        {
            ScrollArea.bar(context.draw, this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.A50);
        }

        if (this.dragging && this.callback != null)
        {
            this.callback.accept(context);
        }
    }
}