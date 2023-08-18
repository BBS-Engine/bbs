package mchorse.studio;

import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;

public class StudioShaders
{
    private ShaderPipeline pipeline;

    public Framebuffer gbuffer;

    public StudioShaders(ShaderPipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public void reload()
    {

    }
}