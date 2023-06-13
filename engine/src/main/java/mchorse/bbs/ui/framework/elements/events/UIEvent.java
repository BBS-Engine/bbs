package mchorse.bbs.ui.framework.elements.events;

import mchorse.bbs.ui.framework.elements.UIElement;

public abstract class UIEvent <T extends UIElement>
{
    public T element;

    public UIEvent(T element)
    {
        this.element = element;
    }
}