package mchorse.app.ui.welcome;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class UITextureRect extends UIElement
{
    public Link texture;
    public Area rect;
    public String label;

    public UITextureRect(Link texture, Area rect, String label)
    {
        this.texture = texture;
        this.rect = rect;
        this.label = label;
    }

    @Override
    public void render(UIContext context)
    {
        if (this.texture != null)
        {
            Texture texture = context.render.getTextures().getTexture(this.texture);
            boolean hover = this.area.isInside(context);
            int offset = 2;
            int color = hover ? Colors.A50 : Colors.A25;
            int highlight = hover ? Colors.A100 | BBSSettings.primaryColor.get() : Colors.WHITE;

            texture.bind();
            context.batcher.dropShadow(this.area.x + offset, this.area.y + offset, this.area.ex() - offset, this.area.ey() - offset, 6, color, 0);
            context.batcher.texturedBox(texture, highlight, this.area.x, this.area.y, this.rect.w, this.rect.h, this.rect.x, this.rect.y);
            context.batcher.textShadow(this.label, this.area.mx() - (context.font.getWidth(this.label) - 1) / 2, this.area.my() - context.font.getHeight() / 2);
        }

        super.render(context);
    }
}