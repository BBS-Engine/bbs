package mchorse.studio;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.pipeline.ShaderBuffer;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class StudioShaders
{
    private static final float[] CLEAR_COLOR = {0F, 0F, 0F, 0F};
    private static final float[] CLEAR_DEPTH = {1F};

    private ShaderPipeline pipeline;

    public Framebuffer gbuffer;
    public Framebuffer compositePing;
    public Framebuffer compositePong;

    public List<Shader> shaders = new ArrayList<>();

    public StudioShaders(ShaderPipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public void reload()
    {
        /* Clean up */
        if (this.gbuffer != null) this.gbuffer.delete();
        if (this.compositePing != null) this.compositePing.delete();
        if (this.compositePong != null) this.compositePong.delete();

        this.shaders.clear();

        /* Setup */
        this.gbuffer = this.setup(this.pipeline.gbuffers, "g_");
        this.compositePing = this.setup(this.pipeline.composite, "ping_");
        this.compositePong = this.setup(this.pipeline.composite, "pong_");

        for (Link shaderName : this.pipeline.stages)
        {
            Shader stageShader = new Shader(shaderName, VBOAttributes.VERTEX_2D);

            stageShader.onInitialize((shader) ->
            {
                int i = 1;

                UniformInt lightmap = shader.getUniform("u_lightmap", UniformInt.class);

                if (lightmap != null)
                {
                    lightmap.set(0);
                }

                for (ShaderBuffer buffer : this.pipeline.gbuffers)
                {
                    UniformInt uniform = shader.getUniform(buffer.name, UniformInt.class);

                    if (uniform != null)
                    {
                        uniform.set(i);

                        i += 1;
                    }
                }

                for (ShaderBuffer buffer : this.pipeline.composite)
                {
                    UniformInt uniform = shader.getUniform(buffer.name, UniformInt.class);

                    if (uniform != null)
                    {
                        uniform.set(i);

                        i += 1;
                    }
                }
            });

            this.shaders.add(stageShader);
        }
    }

    private Framebuffer setup(List<ShaderBuffer> buffers, String prefix)
    {
        int colors = 0;

        Framebuffer framebuffer = new Framebuffer();

        framebuffer.clearCallback(() ->
        {
            int i = 0;

            for (ShaderBuffer buffer : buffers)
            {
                if (buffer.format.isColor())
                {
                    if (buffer.clear)
                    {
                        GL30.glClearBufferfv(GL30.GL_COLOR, i, CLEAR_COLOR);
                    }

                    i += 1;
                }
                else if (buffer.clear)
                {
                    GL30.glClearBufferfv(GL30.GL_DEPTH, 0, CLEAR_DEPTH);
                }
            }
        });

        for (ShaderBuffer buffer : buffers)
        {
            Texture texture = BBS.getTextures().createTexture(new Link("bbs", prefix + buffer.name));

            texture.bind();
            texture.setFilter(buffer.linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);
            texture.setFormat(buffer.format);

            framebuffer.attach(texture, buffer.format.attachment + (buffer.format.isColor() ? colors : 0));

            if (buffer.format.isColor())
            {
                colors += 1;
            }
        }

        /* Specify draw framebuffer attachments */
        int[] attachments = new int[colors];

        for (int i = 0; i < colors; i++)
        {
            attachments[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
        }

        framebuffer.attachments(attachments);

        return framebuffer;
    }

    public void resize(int w, int h)
    {
        this.gbuffer.resize(w, h);
        this.compositePing.resize(w, h);
        this.compositePong.resize(w, h);
    }
}