package mchorse.bbs.ui.utils.icons;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.utils.colors.Colors;

public class Icon
{
    public final Link link;
    public final String id;
    public final int x;
    public final int y;
    public final int w;
    public final int h;
    public int textureW = 256;
    public int textureH = 256;

    public Icon(Link link, String id, int x, int y)
    {
        this(link, id, x, y, 16, 16);
    }

    public Icon(Link link, String id, int x, int y, int w, int h)
    {
        this.link = link;
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Icon(Link link, String id, int x, int y, int w, int h, int textureW, int textureH)
    {
        this(link, id, x, y, w, h);

        this.textureW = textureW;
        this.textureH = textureH;
    }

    public void bindTexture(UIDraw draw)
    {
        draw.context.getTextures().bind(this.link);
    }

    public void render(UIDraw draw, int x, int y)
    {
        this.render(draw, x, y, Colors.WHITE);
    }

    public void render(UIDraw draw, int x, int y, int color)
    {
        this.render(draw, x, y, color, 0, 0);
    }

    public void render(UIDraw draw, int x, int y, float ax, float ay)
    {
        this.render(draw, x, y, Colors.WHITE, ax, ay);
    }

    public void render(UIDraw draw, int x, int y, int color, float ax, float ay)
    {
        if (this.link == null)
        {
            return;
        }

        x -= ax * this.w;
        y -= ay * this.h;

        this.bindTexture(draw);
        draw.scaledTexturedBox(color, x, y, this.x, this.y, this.w, this.h, this.textureW, this.textureH);
    }

    public void renderArea(UIDraw draw, int x, int y, int w, int h)
    {
        this.renderArea(draw, x, y, w, h, Colors.WHITE);
    }

    public void renderArea(UIDraw draw, int x, int y, int w, int h, int color)
    {
        this.bindTexture(draw);
        draw.texturedArea(color, x, y, w, h, this.x, this.y, this.w, this.h, this.textureW, this.textureH);
    }

    public void fill(UIDraw draw, VAOBuilder builder, int x, int y, int color, float ax, float ay)
    {
        x -= ax * this.w;
        y -= ay * this.h;

        draw.fillTexturedBox(builder, color, x, y, this.x, this.y, this.w, this.h, this.textureW, this.textureH, this.x + this.w, this.y + this.h);
    }

    public void fillArea(UIDraw draw, VAOBuilder builder, int x, int y, int w, int h, int color)
    {
        draw.fillTexturedArea(builder, color, x, y, w, h, this.x, this.y, this.w, this.h, this.textureW, this.textureH);
    }
}