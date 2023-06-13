package mchorse.bbs.world.entities.components;

import mchorse.bbs.graphics.RenderingContext;

public interface IRenderableComponent
{
    public void render(RenderingContext context);

    public default int getRenderPriority()
    {
        return 1;
    }
}