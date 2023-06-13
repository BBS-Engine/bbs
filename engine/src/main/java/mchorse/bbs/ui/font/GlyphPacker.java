package mchorse.bbs.ui.font;

import mchorse.bbs.graphics.text.Glyph;
import mchorse.bbs.ui.utils.Area;
import org.joml.Vector2i;

import java.util.List;
import java.util.Stack;

public class GlyphPacker
{
    public static Vector2i pack(List<Glyph> glyphs, int padding)
    {
        Stack<Area> areas = new Stack<Area>();
        int w = getInitialWidth(glyphs, padding);
        int finalW = 0;
        int finalH = 0;

        glyphs.sort((a, b) -> b.height - a.height);
        areas.add(new Area(0, 0, w, Integer.MAX_VALUE));

        for (Glyph glyph : glyphs)
        {
            for (int i = areas.size() - 1; i >= 0; i--)
            {
                Area area = areas.get(i);

                if (glyph.width > area.w || glyph.height > area.h)
                {
                    continue;
                }

                glyph.x = area.x;
                glyph.y = area.y;

                finalH = Math.max(finalH, glyph.y + glyph.height);
                finalW = Math.max(finalW, glyph.x + glyph.width);

                if (glyph.width == area.w && glyph.height == area.h)
                {
                    Area last = areas.pop();

                    if (i < areas.size())
                    {
                        areas.set(i, last);
                    }
                }
                else if (glyph.height == area.h)
                {
                    area.x += glyph.width;
                    area.w -= glyph.width;

                }
                else if (glyph.width == area.w)
                {
                    area.y += glyph.height;
                    area.h -= glyph.height;

                }
                else
                {
                    areas.add(new Area(area.x + glyph.width, area.y, area.w - glyph.width, glyph.height));

                    area.y += glyph.height;
                    area.h -= glyph.height;
                }

                break;
            }
        }

        /* Remove padding from boxes and add them to the final area */
        if (padding != 0)
        {
            for (Glyph glyph : glyphs)
            {
                glyph.width -= padding;
                glyph.height -= padding;
                glyph.x += padding;
                glyph.y += padding;
            }

            finalW += padding;
            finalH += padding;
        }

        return new Vector2i(finalW, finalH);
    }

    private static int getInitialWidth(List<Glyph> glyphs, int padding)
    {
        int totalArea = 0;
        int maxW = 0;

        for (Glyph glyph : glyphs)
        {
            glyph.width += padding;
            glyph.height += padding;

            totalArea += glyph.width * glyph.height;
            maxW = Math.max(maxW, glyph.width);
        }

        return (int) Math.max(Math.ceil(Math.sqrt(totalArea)), maxW);
    }
}