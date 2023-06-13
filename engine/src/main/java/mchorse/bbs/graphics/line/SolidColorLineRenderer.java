package mchorse.bbs.graphics.line;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.utils.colors.Color;

public class SolidColorLineRenderer implements ILineRenderer
{
    private static SolidColorLineRenderer INSTANCE = new SolidColorLineRenderer();

    private Color color = new Color();

    public static ILineRenderer get(float r, float g, float b, float a)
    {
        return INSTANCE.setColor(r, g, b, a);
    }

    public static ILineRenderer get(Color color)
    {
        return INSTANCE.setColor(color);
    }

    public SolidColorLineRenderer setColor(float r, float g, float b, float a)
    {
        this.color.set(r, g, b, a);

        return this;
    }

    public SolidColorLineRenderer setColor(Color color)
    {
        this.color.copy(color);

        return this;
    }

    @Override
    public void render(VAOBuilder builder, LinePoint point)
    {
        builder.xy(point.x, point.y).rgba(this.color.r, this.color.g, this.color.b, this.color.a);
    }
}