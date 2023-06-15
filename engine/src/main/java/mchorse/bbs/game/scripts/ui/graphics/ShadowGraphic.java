package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class ShadowGraphic extends Graphic
{
    public int secondary = Colors.setA(Colors.WHITE, 0F);
    public int offset;

    public ShadowGraphic()
    {}

    public ShadowGraphic(int x, int y, int w, int h, int primary, int secondary, int offset)
    {
        this.pixels.set(x, y, w, h);
        this.primary = primary;
        this.secondary = secondary;
        this.offset = offset;
    }

    @Override
    protected void renderGraphic(UIContext context, Area area)
    {
        context.batcher.dropShadow(area.x, area.y, area.ex(), area.ey(), this.offset, this.primary, this.secondary);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("secondary", this.secondary);
        data.putInt("offset", this.offset);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.secondary = data.getInt("secondary");
        this.offset = data.getInt("offset");
    }
}