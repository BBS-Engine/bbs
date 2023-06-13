package mchorse.bbs.events;

import mchorse.bbs.graphics.RenderingContext;

public class RenderWorldEvent
{
    public final RenderingContext context;

    public RenderWorldEvent(RenderingContext context)
    {
        this.context = context;
    }
}