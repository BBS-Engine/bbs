package mchorse.bbs.ui.font;

import mchorse.bbs.graphics.text.Glyph;
import mchorse.bbs.utils.resources.Pixels;

public class GlyphData
{
    public Glyph glyph;
    public Pixels pixels;

    public GlyphData(Glyph glyph, Pixels pixels)
    {
        this.glyph = glyph;
        this.pixels = pixels;
    }
}