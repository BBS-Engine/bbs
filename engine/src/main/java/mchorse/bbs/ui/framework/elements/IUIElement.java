package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public interface IUIElement
{
    /**
     * Should be called when position has to be recalculated
     */
    public void resize();

    /**
     * Whether this element is enabled (and can accept any input) 
     */
    public boolean isEnabled();

    /**
     * Whether this element is visible
     */
    public boolean isVisible();

    /**
     * Mouse was clicked
     */
    public boolean mouseClicked(UIContext context);

    /**
     * Mouse wheel was scrolled
     */
    public boolean mouseScrolled(UIContext context);

    /**
     * Mouse was released
     */
    public boolean mouseReleased(UIContext context);

    /**
     * Key was typed
     */
    public boolean keyPressed(UIContext context);

    /**
     * Text was inputted
     */
    public boolean textInput(UIContext context);

    /**
     * Determines whether this element can be rendered on the screen
     */
    public boolean canBeRendered(Area viewport);

    /**
     * Draw its components on the screen
     */
    public void render(UIContext context);
}