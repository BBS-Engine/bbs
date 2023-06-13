package mchorse.bbs.graphics.text.format;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.FontRendererContext;

public class ColorFontFormat extends BaseFontFormat
{
    private int color;

    public ColorFontFormat()
    {}

    public ColorFontFormat(char control, int color)
    {
        super(control);

        this.color = color;
    }

    public int getColor()
    {
        return this.color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    public void reset()
    {}

    @Override
    public void apply(FontRendererContext context)
    {
        super.apply(context);

        context.color.set(this.color);
        context.color.a = 1F;
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.color = data.getInt("color");
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("color", this.color);
    }
}