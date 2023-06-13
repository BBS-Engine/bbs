package mchorse.bbs.graphics.text.format;

import mchorse.bbs.graphics.text.FontRendererContext;

public class WaveFontFormat extends AmountFontFormat
{
    public WaveFontFormat()
    {}

    public WaveFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void process(FontRendererContext context)
    {
        context.y += Math.sin(context.time / 3F + context.index / 2F) * this.amount;
    }
}