package mchorse.bbs.graphics.text.format;

import mchorse.bbs.graphics.text.FontRendererContext;

public class ResetFontFormat extends BaseFontFormat
{
    public ResetFontFormat()
    {}

    public ResetFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void reset()
    {}

    @Override
    public void apply(FontRendererContext context)
    {
        context.reset();
    }
}