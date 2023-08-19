package mchorse.bbs.graphics.shaders.pipeline;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;

import java.util.ArrayList;
import java.util.List;

public class ShaderStage implements IMapSerializable
{
    public Link shader;
    public List<String> input = new ArrayList<>();
    public List<String> output = new ArrayList<>();

    @Override
    public void fromData(MapType data)
    {
        this.input.clear();
        this.output.clear();

        this.shader = LinkUtils.create(data.get("shader"));

        for (BaseType in : data.getList("input"))
        {
            if (in.isString()) this.input.add(in.asString());
        }

        for (BaseType in : data.getList("output"))
        {
            if (in.isString()) this.output.add(in.asString());
        }
    }

    @Override
    public void toData(MapType data)
    {}
}