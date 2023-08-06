package mchorse.bbs.graphics.text;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class Glyph implements IMapSerializable
{
    public char character;
    public int advance;
    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    public int x;
    public int y;
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
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public void fromData(MapType data)
    {
        ListType offset = data.getList("offset");
        ListType tile = data.getList("tile");

        this.advance = data.getInt("advance");
        this.offsetX = offset.getInt(0);
        this.offsetY = offset.getInt(1);
        this.x = tile.getInt(0);
        this.y = tile.getInt(1);
        this.width = tile.getInt(2);
        this.height = tile.getInt(3);
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
        tile.addInt(this.x);
        tile.addInt(this.y);
        tile.addInt(this.width);
        tile.addInt(this.height);

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