package mchorse.bbs.ui.framework.elements.buttons;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.UIUtils;

import java.util.function.Consumer;

public abstract class UIClickable <T> extends UIElement
{
    public Consumer<T> callback;

    protected boolean hover;
    protected boolean pressed;

    public UIClickable(Consumer<T> callback)
    {
        super();

        this.callback = callback;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.isAllowed(context.mouseButton) && this.area.isInside(context))
        {
            this.pressed = true;
            UIUtils.playClick();
            this.click(context.mouseButton);

            return true;
        }

        return super.subMouseClicked(context);
    }

    protected boolean isAllowed(int mouseButton)
    {
        return mouseButton == 0;
    }

    protected void click(int mouseButton)
    {
        if (this.callback != null)
        {
            this.callback.accept(this.get());
        }
    }

    protected abstract T get();

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.pressed = false;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.hover = this.area.isInside(context);

        this.renderSkin(context);
        super.render(context);
    }

    protected abstract void renderSkin(UIContext context);
}