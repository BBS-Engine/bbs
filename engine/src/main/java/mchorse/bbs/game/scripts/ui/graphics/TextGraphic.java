package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public class TextGraphic extends Graphic
{
    public String text = "";

    public TextGraphic()
    {}

    public TextGraphic(String text, int x, int y, int w, int h, int primary, float anchorX, float anchorY)
    {
        this.pixels.set(x, y, w, h);
        this.primary = primary;
        this.text = text;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    @Override
    public void renderGraphic(UIContext context, Area area)
    {
        FontRenderer font = context.font;

        String text = TextUtils.processColoredText(this.text);
        int w = font.getWidth(text);
        int left = area.x(this.anchorX) - (int) (w * this.anchorX);
        int top = area.y(this.anchorY) - (int) (font.getHeight() * this.anchorY);

        font.renderWithShadow(context.render, text, left, top, this.primary);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("text", this.text);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.text = data.getString("text");
    }
}