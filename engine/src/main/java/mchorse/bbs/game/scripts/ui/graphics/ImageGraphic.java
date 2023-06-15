package mchorse.bbs.game.scripts.ui.graphics;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.resources.LinkUtils;

public class ImageGraphic extends Graphic
{
    public Link picture;
    public int width;
    public int height;

    public ImageGraphic()
    {}

    public ImageGraphic(Link picture, int x, int y, int w, int h, int width, int height, int primary)
    {
        this.picture = picture;
        this.pixels.set(x, y, w, h);
        this.primary = primary;
        this.width = width;
        this.height = height;
    }

    @Override
    public void renderGraphic(UIContext context, Area area)
    {
        if (this.picture != null)
        {
            Texture texture = context.render.getTextures().getTexture(this.picture);
            int left = area.x;
            int top = area.y;

            context.batcher.texturedBox(texture, this.primary, left, top, area.w, area.h, 0, 0, texture.width, texture.height, texture.width, texture.height);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.picture != null)
        {
            data.put("image", LinkUtils.toData(this.picture));
        }

        data.putInt("width", this.width);
        data.putInt("height", this.height);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.picture = LinkUtils.create(data.get("image"));
        this.width = data.getInt("width");
        this.height = data.getInt("height");
    }
}