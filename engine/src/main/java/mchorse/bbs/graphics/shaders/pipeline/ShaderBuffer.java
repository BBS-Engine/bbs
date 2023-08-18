package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.texture.TextureFormat;

public class ShaderBuffer implements IMapSerializable
{
    public String name = "";
    public TextureFormat format = TextureFormat.RGBA_U8;
    public boolean clear = true;

    @Override
    public void fromData(MapType data)
    {
        this.name = data.getString("name");
        this.format = TextureFormat.getByName(data.getString("format"));
        this.clear = data.getBool("clear", true);
    }

    @Override
    public void toData(MapType data)
    {}
}