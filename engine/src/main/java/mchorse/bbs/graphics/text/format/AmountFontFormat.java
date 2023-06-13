package mchorse.bbs.graphics.text.format;

import mchorse.bbs.graphics.text.FontRendererContext;

public abstract class AmountFontFormat extends BaseFontFormat
{
    protected int amount;

    public AmountFontFormat()
    {}

    public AmountFontFormat(char control)
    {
        super(control);
    }

    @Override
    public void reset()
    {
        this.amount = 0;
    }

    @Override
    public void apply(FontRendererContext context)
    {
        super.apply(context);

        this.amount += 1;
    }
}