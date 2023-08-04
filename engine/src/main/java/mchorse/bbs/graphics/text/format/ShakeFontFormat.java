package mchorse.bbs.graphics.text.format;

import mchorse.bbs.graphics.text.FontRendererContext;

public class ShakeFontFormat extends AmountFontFormat
{
    public ShakeFontFormat()
    {}

    public ShakeFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void process(FontRendererContext context)
    {
        context.x += (context.random.nextFloat() - 0.5F) * this.amount;
        context.y += (context.random.nextFloat() - 0.5F) * this.amount;
    }
}