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
        context.x += Math.random() * this.amount;
        context.y += Math.random() * this.amount;
    }
}