package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class ShaderPipelineFramebuffer implements IMapSerializable
{
    public List<ShaderFramebufferTexture> textures = new ArrayList<ShaderFramebufferTexture>();

    @Override
    public void fromData(MapType data)
    {
        this.textures.clear();

        ListType texturesData = data.getList("textures");

        for (BaseType type : texturesData)
        {
            if (!type.isMap())
            {
                continue;
            }

            MapType textureData = type.asMap();
            ShaderFramebufferTexture texture = new ShaderFramebufferTexture();

            texture.fromData(textureData);

            this.textures.add(texture);
        }
    }

    @Override
    public void toData(MapType data)
    {

    }
}