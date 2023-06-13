package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IUIElement;
import mchorse.bbs.ui.utils.Area;

import java.util.function.Consumer;

public class UIRenderable implements IUIElement
{
    public Consumer<UIContext> callback;

    public UIRenderable(Consumer<UIContext> callback)
    {
        this.callback = callback;
    }

    @Override
    public void resize()
    {}

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public boolean mouseClicked(UIContext context)
    {
        return false;
    }

    @Override
    public boolean mouseScrolled(UIContext context)
    {
        return false;
    }

    @Override
    public boolean mouseReleased(UIContext context)
    {
        return false;
    }

    @Override
    public boolean keyPressed(UIContext context)
    {
        return false;
    }

    @Override
    public boolean textInput(UIContext context)
    {
        return false;
    }

    @Override
    public boolean canBeRendered(Area viewport)
    {
        return true;
    }

    @Override
    public void render(UIContext context)
    {
        if (this.callback != null)
        {
            this.callback.accept(context);
        }
    }
}