package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;

public class IconGraphic extends Graphic
{
    public String id = "";

    public IconGraphic()
    {}

    public IconGraphic(String id, int x, int y, int primary, float anchorX, float anchorY)
    {
        this.pixels.set(x - 8, y - 8, 16, 16);

        this.primary = primary;
        this.id = id;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    @Override
    public void renderGraphic(UIContext context, Area area)
    {
        Icon icon = Icons.ICONS.get(this.id);
        int left = area.x(0.5F);
        int top = area.y(0.5F);

        if (icon != null)
        {
            icon.render(context.draw, left, top, this.primary, 0.5F, 0.5F);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("icon", this.id);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.id = data.getString("icon");
    }
}