package mchorse.bbs.graphics.text.format;

import mchorse.bbs.graphics.text.FontRendererContext;

public class BoldFontFormat extends BaseFontFormat
{
    public BoldFontFormat()
    {
        super();
    }

    public BoldFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void reset()
    {}

    @Override
    public void apply(FontRendererContext context)
    {
        super.apply(context);

        context.bold = true;
    }
}