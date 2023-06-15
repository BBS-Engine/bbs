package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public class RectGraphic extends Graphic
{
    public RectGraphic()
    {}

    public RectGraphic(int x, int y, int w, int h, int primary)
    {
        this.pixels.set(x, y, w, h);
        this.primary = primary;
    }

    @Override
    public void renderGraphic(UIContext context, Area area)
    {
        context.batcher.box(area.x, area.y, area.ex(), area.ey(), this.primary);
    }
}