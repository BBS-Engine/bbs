package mchorse.bbs.ui.framework.elements.context;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

public abstract class UIContextMenu extends UIElement
{
    public UIContextMenu()
    {
        super();

        this.eventPropagataion(EventPropagation.BLOCK_INSIDE);
    }

    public abstract boolean isEmpty();

    /**
     * Set mouse coordinate
     *
     * In this method for subclasses, you should setup the resizer
     */
    public abstract void setMouse(UIContext context);

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (!this.area.isInside(context))
        {
            this.removeFromParent();
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.removeFromParent();

            return true;
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A100);

        super.render(context);
    }
}