package mchorse.bbs.graphics.text;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.format.BoldFontFormat;
import mchorse.bbs.graphics.text.format.ColorFontFormat;
import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.graphics.text.format.ItalicFontFormat;
import mchorse.bbs.graphics.text.format.RainbowFontFormat;
import mchorse.bbs.graphics.text.format.ResetFontFormat;
import mchorse.bbs.graphics.text.format.ShakeFontFormat;
import mchorse.bbs.graphics.text.format.WaveFontFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Font implements IMapSerializable
{
    public String name;
    public int height;
    public Glyph[] glyphs;

    public final Map<Integer, IFontFormat> formats = new HashMap<>();

    public char boldChar;
    public char resetChar;

    public static Font fromMap(MapType map)
    {
        Font font = new Font();

        font.fromData(map);

        return font;
    }

    public Font()
    {
        this.registerFontFormat(new ColorFontFormat('0', 0xff000000));
        this.registerFontFormat(new ColorFontFormat('1', 0xff3d3d3d));
        this.registerFontFormat(new ColorFontFormat('2', 0xff858585));
        this.registerFontFormat(new ColorFontFormat('3', 0xffffffff));
        this.registerFontFormat(new ColorFontFormat('4', 0xff891e2b));
        this.registerFontFormat(new ColorFontFormat('5', 0xffff0040));
        this.registerFontFormat(new ColorFontFormat('6', 0xff8e251d));
        this.registerFontFormat(new ColorFontFormat('7', 0xffff5000));
        this.registerFontFormat(new ColorFontFormat('8', 0xffffeb57));
        this.registerFontFormat(new ColorFontFormat('9', 0xff1e6f50));
        this.registerFontFormat(new ColorFontFormat('a', 0xff5ac54f));
        this.registerFontFormat(new ColorFontFormat('b', 0xff00396d));
        this.registerFontFormat(new ColorFontFormat('c', 0xff0098dc));
        this.registerFontFormat(new ColorFontFormat('d', 0xff7a09fa));
        this.registerFontFormat(new ColorFontFormat('e', 0xfff389f5));
        this.registerFontFormat(new ItalicFontFormat('i'));
        this.registerFontFormat(new BoldFontFormat('b'));
        this.registerFontFormat(new ShakeFontFormat('s'));
        this.registerFontFormat(new WaveFontFormat('w'));
        this.registerFontFormat(new RainbowFontFormat('n'));
        this.registerFontFormat(new ResetFontFormat('r'));
    }

    private void registerFontFormat(IFontFormat format)
    {
        if (format instanceof BoldFontFormat)
        {
            this.boldChar = format.getControlCharacter();
        }
        else if (format instanceof ResetFontFormat)
        {
            this.resetChar = format.getControlCharacter();
        }

        this.formats.put((int) format.getControlCharacter(), format);
    }

    public void setupGlyphs(List<Glyph> glyphs)
    {
        int max = 0;

        for (Glyph glyph : glyphs)
        {
            max = Math.max(max, glyph.character);
        }

        Glyph[] array = new Glyph[max + 1];

        for (Glyph glyph : glyphs)
        {
            array[glyph.character] = glyph;
        }

        this.glyphs = array;
    }

    /**
     * Get glyph by given character 
     */
    public Glyph getGlyph(char glyph)
    {
        if (glyph >= this.glyphs.length)
        {
            return null;
        }

        return this.glyphs[glyph];
    }

    /**
     * Get kerning of the right character relative to the left one 
     */
    public int getKerning(char left, char right)
    {
        Glyph l = this.getGlyph(left);

        if (l == null || l.kernings.isEmpty())
        {
            return 0;
        }

        for (Kerning kern : l.kernings)
        {
            if (kern.right == right)
            {
                return kern.kerning;
            }
        }

        return 0;
    }

    @Override
    public void fromData(MapType map)
    {
        this.formats.clear();
        this.boldChar = '\0';
        this.resetChar = '\0';

        this.name = map.getString("name");
        this.height = map.getInt("height");

        MapType formats = map.getMap("formats");

        for (String key : formats.keys())
        {
            BaseType baseType = formats.get(key);

            if (baseType.isMap())
            {
                IFontFormat format = BBS.getFactoryFontFormats().fromData(baseType.asMap());

                if (format != null)
                {
                    format.setControlCharacter(key);
                    this.registerFontFormat(format);
                }
            }
        }

        MapType glyphData = map.getMap("glyphs");
        List<Glyph> glyphs = new ArrayList<>();

        for (String key : glyphData.keys())
        {
            Glyph glyph = new Glyph();

            glyph.fromData(glyphData.getMap(key));
            glyph.character = (char) Integer.parseInt(key);
            glyphs.add(glyph);
        }

        this.setupGlyphs(glyphs);
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("name", this.name);
        data.putInt("height", this.height);

        MapType formats = new MapType(false);
        MapType glyphs = new MapType(false);

        for (IFontFormat format : this.formats.values())
        {
            formats.put(String.valueOf(format.getControlCharacter()), BBS.getFactoryFontFormats().toData(format));
        }

        for (Glyph glyph : this.glyphs)
        {
            if (glyph != null)
            {
                MapType glyphData = glyph.toData();

                glyphs.put(String.valueOf((int) glyph.character), glyphData);
            }
        }

        data.put("formats", formats);
        data.put("glyphs", glyphs);
    }
}