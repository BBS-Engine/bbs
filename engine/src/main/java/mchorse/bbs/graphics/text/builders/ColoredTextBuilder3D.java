package mchorse.bbs.graphics.text.builders;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.colors.Color;
import org.joml.Vector3f;

public class ColoredTextBuilder3D extends BaseColoredTextBuilder
{
    private Vector3f offset = new Vector3f();

    public ColoredTextBuilder3D setup(int color)
    {
        return this.setup(color, 0, 0, 0);
    }

    public ColoredTextBuilder3D setup(int color, float x, float y, float z)
    {
        this.color.set(color);
        this.offset.set(x, y, z);

        return this;
    }

    @Override
    public VBOAttributes getAttributes()
    {
        return VBOAttributes.VERTEX_NORMAL_UV_RGBA;
    }

    @Override
    public VAOBuilder put(VAOBuilder builder, float x, float y, float u, float v, float tw, float th, Color color)
    {
        if (this.multiply)
        {
            return builder.xyz(x + this.offset.x, y + this.offset.y, this.offset.z)
                .xyz(0F, 0F, 1F)
                .uv(u, v, tw, th)
                .rgba(this.color.r * color.r, this.color.g * color.g, this.color.b * color.b, this.color.a * color.a);
        }

        Color c = this.color;

        if (color.r < 1F || color.g < 1F || color.b < 1F)
        {
            c = color;
        }

        return builder.xyz(x + this.offset.x, y + this.offset.y, this.offset.z)
            .xyz(0F, 0F, 1F)
            .uv(u, v, tw, th)
            .rgba(c.r, c.g, c.b, this.color.a * color.a);
    }
}