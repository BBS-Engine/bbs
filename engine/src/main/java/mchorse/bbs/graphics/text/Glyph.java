package mchorse.bbs.graphics.text;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.utils.Area;

import java.util.ArrayList;
import java.util.List;

public class Glyph implements IMapSerializable
{
    public char character;
    public int advance;
    public int offsetX;
    public int offsetY;
    public Area tile = new Area();
    public boolean emoji;
    public List<Kerning> kernings = new ArrayList<>();

    private static void parseKernings(MapType data, List<Kerning> kernings)
    {
        for (String key : data.keys())
        {
            kernings.add(new Kerning((char) Integer.parseInt(key), data.getInt(key)));
        }
    }

    public Glyph()
    {}

    public Glyph(int x, int y, int w, int h)
    {
        this.tile.set(x, y, w, h);
    }

    @Override
    public void fromData(MapType data)
    {
        ListType offset = data.getList("offset");
        ListType tile = data.getList("tile");

        this.advance = data.getInt("advance");
        this.offsetX = offset.getInt(0);
        this.offsetY = offset.getInt(1);
        this.tile.set(tile.getInt(0), tile.getInt(1), tile.getInt(2), tile.getInt(3));
        this.emoji = data.getBool("emoji");

        if (data.has("kerning"))
        {
            this.kernings.clear();

            parseKernings(data.getMap("kerning"), this.kernings);
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType offset = new ListType();
        ListType tile = new ListType();

        offset.addInt(this.offsetX);
        offset.addInt(this.offsetY);
        tile.addInt(this.tile.x);
        tile.addInt(this.tile.y);
        tile.addInt(this.tile.w);
        tile.addInt(this.tile.h);

        data.put("offset", offset);
        data.put("tile", tile);
        data.putInt("advance", this.advance);
        data.putBool("emoji", this.emoji);

        if (this.kernings != null)
        {
            MapType kernings = new MapType(false);

            for (Kerning kerning : this.kernings)
            {
                kernings.putInt(String.valueOf((int) kerning.right), kerning.kerning);
            }

            data.put("kerning", kernings);
        }
    }
}