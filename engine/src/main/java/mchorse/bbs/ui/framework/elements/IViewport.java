package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.ui.framework.elements.utils.IViewportStack;

public interface IViewport
{
    public void apply(IViewportStack stack);

    public void unapply(IViewportStack stack);
}