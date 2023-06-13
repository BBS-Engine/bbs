package mchorse.bbs.events;

import mchorse.bbs.ui.framework.UIRenderingContext;

public class RenderHUDEvent
{
    public final UIRenderingContext context;
    public final int w;
    public final int h;

    public RenderHUDEvent(UIRenderingContext context, int w, int h)
    {
        this.context = context;
        this.w = w;
        this.h = h;
    }
}