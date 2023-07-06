package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.texture.TextureFormat;

public class ShaderFramebufferTexture implements IMapSerializable
{
    public String name;
    public TextureFormat format;

    @Override
    public void fromData(MapType data)
    {
        this.name = data.getString("name");
        this.format = TextureFormat.getByName(data.getString("format"));
    }

    @Override
    public void toData(MapType data)
    {

    }
}