package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;

import java.util.ArrayList;
import java.util.List;

public class ShaderPipeline implements IMapSerializable
{
    public List<ShaderBuffer> gbuffers = new ArrayList<>();
    public List<ShaderBuffer> composite = new ArrayList<>();
    public List<Link> stages = new ArrayList<>();

    @Override
    public void fromData(MapType data)
    {
        this.gbuffers.clear();
        this.composite.clear();
        this.stages.clear();

        ListType gbuffers = data.getList("gbuffers");

        for (BaseType gbuffer : gbuffers)
        {
            if (gbuffer.isMap())
            {
                ShaderBuffer shaderBuffer = new ShaderBuffer();

                shaderBuffer.fromData(gbuffer.asMap());
                this.gbuffers.add(shaderBuffer);
            }
        }

        ListType composites = data.getList("composite");

        for (BaseType composite : composites)
        {
            if (composite.isMap())
            {
                ShaderBuffer shaderBuffer = new ShaderBuffer();

                shaderBuffer.fromData(composite.asMap());
                this.composite.add(shaderBuffer);
            }
        }

        ListType stages = data.getList("stages");

        for (BaseType stage : stages)
        {
            Link link = LinkUtils.create(stage);

            if (link != null)
            {
                this.stages.add(link);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {}
}