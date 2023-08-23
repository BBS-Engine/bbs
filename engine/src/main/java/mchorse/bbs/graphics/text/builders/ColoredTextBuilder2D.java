package mchorse.bbs.graphics.text.builders;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.colors.Color;

public class ColoredTextBuilder2D extends BaseColoredTextBuilder
{
    public ColoredTextBuilder2D color(int color)
    {
        this.color.set(color);

        return this;
    }

    @Override
    public VBOAttributes getAttributes()
    {
        return VBOAttributes.VERTEX_UV_RGBA_2D;
    }

    @Override
    public VAOBuilder put(VAOBuilder builder, float x, float y, float u, float v, float tw, float th, Color color)
    {
        if (this.multiply)
        {
            return builder.xy(x, y).uv(u, v, tw, th).rgba(this.color.r * color.r, this.color.g * color.g, this.color.b * color.b, this.color.a * color.a);
        }

        Color c = this.color;

        if (color.r < 1F || color.g < 1F || color.b < 1F)
        {
            c = color;
        }

        return builder.xy(x, y).uv(u, v, tw, th).rgba(c.r, c.g, c.b, this.color.a * color.a);
    }
}