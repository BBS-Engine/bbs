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
    public List<ShaderBuffer> composite = new ArrayList<>();
    public List<ShaderStage> stages = new ArrayList<>();

    public boolean shadowMap;
    public int shadowResolution = 1024;

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
            if (stage.isMap())
            {
                ShaderStage shaderStage = new ShaderStage();

                shaderStage.fromData(stage.asMap());
                this.stages.add(shaderStage);
            }
        }
        
        MapType shadowMap = data.getMap("shadow");

        this.shadowMap = shadowMap.getBool("enabled", false);
        this.shadowResolution = shadowMap.getInt("resolution", 1024);
    }

    @Override
    public void toData(MapType data)
    {}
}