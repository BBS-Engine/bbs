package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;

import java.util.ArrayList;
import java.util.List;

public class ShaderStage implements IMapSerializable
{
    public Link shader;
    public List<ShaderBuffer> buffers = new ArrayList<>();

    @Override
    public void fromData(MapType data)
    {
        this.shader = LinkUtils.create(data.get("shader"));

        this.buffers.clear();

        ListType buffers = data.getList("buffers");

        for (BaseType buffer : buffers)
        {
            if (buffer.isMap())
            {
                ShaderBuffer shaderBuffer = new ShaderBuffer();

                shaderBuffer.fromData(buffer.asMap());
                this.buffers.add(shaderBuffer);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {}
}