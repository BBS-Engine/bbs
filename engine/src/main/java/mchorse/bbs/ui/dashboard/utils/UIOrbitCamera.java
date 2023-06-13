package mchorse.bbs.ui.dashboard.utils;

import mchorse.bbs.camera.OrbitCamera;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IUIElement;
import mchorse.bbs.ui.utils.Area;

public class UIOrbitCamera implements IUIElement
{
    public OrbitCamera orbit = new OrbitCamera();
    private boolean control;

    public boolean canControl()
    {
        return this.control;
    }

    public void setControl(boolean control)
    {
        this.control = control;
    }

    public boolean animate(UIContext context)
    {
        if (!this.control)
        {
            this.orbit.cache(context.mouseX, context.mouseY);

            return false;
        }

        boolean dragged = this.orbit.drag(context.mouseX, context.mouseY);
        boolean moved = this.orbit.update(context);

        return dragged || moved;
    }

    @Override
    public boolean mouseClicked(UIContext context)
    {
        if (this.orbit.canStart(context))
        {
            this.orbit.start(context.mouseX, context.mouseY);

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(UIContext context)
    {
        if (!this.control)
        {
            return false;
        }

        return this.orbit.scroll(context.mouseWheel);
    }

    @Override
    public boolean mouseReleased(UIContext context)
    {
        this.orbit.release();

        return false;
    }

    @Override
    public void render(UIContext context)
    {
        this.animate(context);
    }

    /* Unimplemented GUI element methods */

    @Override
    public void resize()
    {}

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public boolean keyPressed(UIContext context)
    {
        return this.control && this.orbit.keyPressed(context);
    }

    @Override
    public boolean textInput(UIContext context)
    {
        return false;
    }

    @Override
    public boolean canBeRendered(Area area)
    {
        return true;
    }
}
