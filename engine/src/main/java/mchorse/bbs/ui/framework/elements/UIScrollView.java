package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.IViewportStack;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.ui.utils.ScrollDirection;

import java.util.function.Consumer;

/**
 * Scroll area GUI class
 * 
 * This bad boy allows to scroll stuff
 */
public class UIScrollView extends UIElement implements IViewport
{
    public ScrollArea scroll;

    public Consumer<UIContext> preRenderCallback;

    public UIScrollView()
    {
        this(ScrollDirection.VERTICAL);
    }

    public UIScrollView(ScrollDirection direction)
    {
        super();

        this.area = this.scroll = new ScrollArea(0);
        this.scroll.direction = direction;
        this.scroll.scrollSpeed = 20;
    }

    public UIScrollView preRender(Consumer<UIContext> callback)
    {
        this.preRenderCallback = callback;

        return this;
    }

    @Override
    public void apply(IViewportStack stack)
    {
        stack.pushViewport(this.area);

        if (this.scroll.direction == ScrollDirection.VERTICAL)
        {
            stack.shiftY(this.scroll.scroll);
        }
        else
        {
            stack.shiftX(this.scroll.scroll);
        }
    }

    @Override
    public void unapply(IViewportStack stack)
    {
        if (this.scroll.direction == ScrollDirection.VERTICAL)
        {
            stack.shiftY(-this.scroll.scroll);
        }
        else
        {
            stack.shiftX(-this.scroll.scroll);
        }

        stack.popViewport();
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.clamp();
    }

    @Override
    protected boolean childrenMouseClicked(UIContext context)
    {
        if (!this.area.isInside(context))
        {
            if (context.isFocused() && this.isDescendant((UIElement) context.activeElement))
            {
                context.unfocus();
            }

            return false;
        }

        if (this.scroll.mouseClicked(context))
        {
            return true;
        }

        this.apply(context);
        boolean result = super.childrenMouseClicked(context);
        this.unapply(context);

        return result;
    }

    @Override
    protected boolean childrenMouseScrolled(UIContext context)
    {
        if (!this.area.isInside(context))
        {
            if (context.isFocused() && this.isDescendant((UIElement) context.activeElement))
            {
                context.unfocus();
            }

            return false;
        }

        this.apply(context);
        boolean result = super.childrenMouseScrolled(context);
        this.unapply(context);

        if (result)
        {
            return true;
        }

        return this.scroll.mouseScroll(context);
    }

    @Override
    protected boolean childrenMouseReleased(UIContext context)
    {
        this.scroll.mouseReleased(context);

        this.apply(context);
        boolean result = super.childrenMouseReleased(context);
        this.unapply(context);

        return result;
    }

    @Override
    protected boolean childrenKeyPressed(UIContext context)
    {
        this.apply(context);
        boolean result = super.childrenKeyPressed(context);
        this.unapply(context);

        return result;
    }

    @Override
    protected boolean childrenTextInput(UIContext context)
    {
        this.apply(context);
        boolean result = super.childrenTextInput(context);
        this.unapply(context);

        return result;
    }

    @Override
    public void render(UIContext context)
    {
        UIElement lastTooltip = context.tooltip.element;

        this.scroll.drag(context.mouseX, context.mouseY);

        context.batcher.clip(this.scroll, context);

        this.apply(context);

        this.preRender(context);
        super.render(context);
        this.postRender(context);

        this.unapply(context);

        this.scroll.renderScrollbar(context.batcher);

        context.batcher.unclip(context);

        /* Clear tooltip in case if it was set outside of scroll area within the scroll */
        if (!this.area.isInside(context) && context.tooltip.element != lastTooltip)
        {
            context.tooltip.set(context, null);
        }
    }

    protected void preRender(UIContext context)
    {
        if (this.preRenderCallback != null)
        {
            this.preRenderCallback.accept(context);
        }
    }

    protected void postRender(UIContext context)
    {}
}