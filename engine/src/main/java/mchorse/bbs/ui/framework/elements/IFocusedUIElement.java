package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.ui.framework.UIContext;

public interface IFocusedUIElement
{
    public boolean isFocused();

    public void focus(UIContext context);

    public void unfocus(UIContext context);

    public void selectAll(UIContext context);

    public void unselect(UIContext context);
}