package mchorse.bbs.graphics.text.format;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.graphics.text.FontRendererContext;

public interface IFontFormat extends IMapSerializable
{
    public char getControlCharacter();

    public void setControlCharacter(String string);

    public void reset();

    public void apply(FontRendererContext context);

    public void process(FontRendererContext context);
}