package mchorse.bbs.utils.resources;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Colors;

import java.util.Objects;

public class FilteredLink implements IWritableLink
{
    public Link path;

    public boolean autoSize = true;
    public int sizeW;
    public int sizeH;

    public int color = Colors.WHITE;
    public float scale = 1F;
    public boolean scaleToLargest;
    public int shiftX;
    public int shiftY;

    /* Filters */
    public int pixelate = 1;
    public boolean erase;

    public static FilteredLink from(BaseType data)
    {
        try
        {
            FilteredLink location = new FilteredLink();

            location.fromData(data);

            return location;
        }
        catch (Exception e)
        {}

        return null;
    }

    public FilteredLink()
    {}

    public FilteredLink(Link path)
    {
        this.path = path;
    }

    public int getWidth(int width)
    {
        if (!this.autoSize && this.sizeW > 0)
        {
            return this.sizeW;
        }

        return width;
    }

    public int getHeight(int height)
    {
        if (!this.autoSize && this.sizeH > 0)
        {
            return this.sizeH;
        }

        return height;
    }

    @Override
    public String toString()
    {
        return this.path == null ? "" : this.path.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof FilteredLink)
        {
            FilteredLink filtered = (FilteredLink) obj;

            return Objects.equals(this.path, filtered.path)
                && this.autoSize == filtered.autoSize
                && this.sizeW == filtered.sizeW
                && this.sizeH == filtered.sizeH
                && this.scaleToLargest == filtered.scaleToLargest
                && this.color == filtered.color
                && this.scale == filtered.scale
                && this.shiftX == filtered.shiftX
                && this.shiftY == filtered.shiftY
                && this.pixelate == filtered.pixelate
                && this.erase == filtered.erase;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int hashCode = this.path.hashCode();

        hashCode = 31 * hashCode + (this.autoSize ? 1 : 0);
        hashCode = 31 * hashCode + this.sizeW;
        hashCode = 31 * hashCode + this.sizeH;
        hashCode = 31 * hashCode + (this.scaleToLargest ? 1 : 0);
        hashCode = 31 * hashCode + this.color;
        hashCode = 31 * hashCode + (int) (this.scale * 1000);
        hashCode = 31 * hashCode + this.shiftX;
        hashCode = 31 * hashCode + this.shiftY;
        hashCode = 31 * hashCode + this.pixelate;
        hashCode = 31 * hashCode + (this.erase ? 1 : 0);

        return hashCode;
    }

    public boolean isDefault()
    {
        return (this.autoSize || (this.sizeW == 0 && this.sizeH == 0)) && this.color == Colors.WHITE && !this.scaleToLargest && this.scale == 1F && this.shiftX == 0 && this.shiftY == 0 && this.pixelate <= 1 && !this.erase;
    }

    @Override
    public BaseType toData()
    {
        MapType map = new MapType();

        map.putString("path", this.toString());

        if (this.color != Colors.WHITE) map.putInt("color", this.color);
        if (this.scale != 1) map.putFloat("scale", this.scale);
        if (this.scaleToLargest) map.putBool("scaleToLargest", this.scaleToLargest);
        if (this.shiftX != 0) map.putInt("shiftX", this.shiftX);
        if (this.shiftY != 0) map.putInt("shiftY", this.shiftY);
        if (this.pixelate > 1) map.putInt("pixelate", this.pixelate);
        if (this.erase) map.putBool("erase", this.erase);
        if (!this.autoSize) map.putBool("autoSize", this.autoSize);
        if (this.sizeW > 0) map.putInt("sizeW", this.sizeW);
        if (this.sizeH > 0) map.putInt("sizeH", this.sizeH);

        return map;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (BaseType.isString(data))
        {
            this.path = LinkUtils.create(data);

            return;
        }

        MapType map = (MapType) data;

        this.path = LinkUtils.create(map.getString("path"));

        if (map.has("color")) this.color = map.getInt("color");
        if (map.has("scale")) this.scale = map.getFloat("scale");
        if (map.has("scaleToLargest")) this.scaleToLargest = map.getBool("scaleToLargest");
        if (map.has("shiftX")) this.shiftX = map.getInt("shiftX");
        if (map.has("shiftY")) this.shiftY = map.getInt("shiftY");
        if (map.has("pixelate")) this.pixelate = map.getInt("pixelate");
        if (map.has("erase")) this.erase = map.getBool("erase");
        if (map.has("autoSize")) this.autoSize = map.getBool("autoSize");
        if (map.has("sizeW")) this.sizeW = map.getInt("sizeW");
        if (map.has("sizeH")) this.sizeH = map.getInt("sizeH");
    }

    @Override
    public Link copy()
    {
        return LinkUtils.copy(this.path);
    }

    public FilteredLink copyFiltered()
    {
        return FilteredLink.from(this.toData());
    }
}