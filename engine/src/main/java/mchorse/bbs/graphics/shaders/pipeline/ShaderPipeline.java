package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class ShaderPipeline implements IMapSerializable
{
    public List<ShaderBuffer> gbuffers = new ArrayList<>();
    public List<ShaderStage> compositeStages = new ArrayList<>();

    @Override
    public void fromData(MapType data)
    {
        this.gbuffers.clear();
        this.compositeStages.clear();

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

        MapType composite = data.getMap("composite");
        ListType stages = composite.getList("stages");

        for (BaseType stage : stages)
        {
            if (stage.isMap())
            {
                ShaderStage shaderStage = new ShaderStage();

                shaderStage.fromData(stage.asMap());
                this.compositeStages.add(shaderStage);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {}
}