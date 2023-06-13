package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class GradientGraphic extends Graphic
{
    public int secondary = Colors.A100;
    public boolean horizontal;

    public GradientGraphic()
    {}

    public GradientGraphic(int x, int y, int w, int h, int primary, int secondary, boolean horizontal)
    {
        this.pixels.set(x, y, w, h);
        this.primary = primary;
        this.secondary = secondary;
        this.horizontal = horizontal;
    }

    @Override
    protected void renderGraphic(UIContext context, Area area)
    {
        if (this.horizontal)
        {
            context.draw.gradientHBox(area.x, area.y, area.ex(), area.ey(), this.primary, this.secondary);
        }
        else
        {
            context.draw.gradientVBox(area.x, area.y, area.ex(), area.ey(), this.primary, this.secondary);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("secondary", this.secondary);
        data.putBool("horizontal", this.horizontal);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.secondary = data.getInt("secondary");
        this.horizontal = data.getBool("horizontal");
    }
}