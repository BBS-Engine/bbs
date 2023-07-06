package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;

import java.util.HashMap;
import java.util.Map;

public class ShaderPipeline implements IMapSerializable
{
    public boolean deferred;
    public Map<String, Link> shaders = new HashMap<String, Link>();
    public ShaderPipelineFramebuffer gbuffer = new ShaderPipelineFramebuffer();

    @Override
    public void fromData(MapType data)
    {
        this.deferred = data.getBool("deferred");

        MapType shaders = data.getMap("shaders");

        for (String key : shaders.keys())
        {
            String value = shaders.getString(key);
            Link link = Link.create(value);

            this.shaders.put(key, link);
        }

        MapType framebuffers = data.getMap("framebuffers");

        if (framebuffers.has("gbuffer", BaseType.TYPE_MAP))
        {
            this.gbuffer.fromData(framebuffers.getMap("gbuffer"));
        }
    }

    @Override
    public void toData(MapType data)
    {

    }
}