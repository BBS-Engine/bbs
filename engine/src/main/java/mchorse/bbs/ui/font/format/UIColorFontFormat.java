package mchorse.bbs.ui.font.format;

import mchorse.bbs.graphics.text.format.ColorFontFormat;
import mchorse.bbs.ui.framework.elements.input.UIColor;

public class UIColorFontFormat extends UIBaseFontFormat<ColorFontFormat>
{
    public UIColor color;

    public UIColorFontFormat()
    {
        super();

        this.color = new UIColor((c) -> this.format.setColor(c));
        this.color.w(60);

        this.add(this.color);
    }

    @Override
    public void fill(ColorFontFormat format)
    {
        super.fill(format);

        this.color.setColor(this.format.getColor());
    }
}