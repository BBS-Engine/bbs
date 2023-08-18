package mchorse.studio;

import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.pipeline.ShaderBuffer;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.pipeline.ShaderStage;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
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
    public List<Stage> stages = new ArrayList<>();

    public StudioShaders(ShaderPipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public void reload()
    {
        /* Clean up */
        if (this.gbuffer != null)
        {
            this.gbuffer.delete();
        }

        for (Stage stage : this.stages)
        {
            stage.framebuffer.delete();
        }

        this.stages.clear();

        /* Setup */
        this.gbuffer = this.setup(this.pipeline.gbuffers);

        ShaderStage previous = null;

        for (ShaderStage shaderStage : this.pipeline.compositeStages)
        {
            Stage stage = new Stage();
            ShaderStage previousStage = previous;

            stage.buffers = shaderStage.buffers;
            stage.framebuffer = this.setup(shaderStage.buffers);
            stage.shader = new Shader(shaderStage.shader, VBOAttributes.VERTEX_2D);
            stage.shader.onInitialize((shader) ->
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

                if (previousStage != null)
                {
                    for (ShaderBuffer buffer : previousStage.buffers)
                    {
                        UniformInt uniform = shader.getUniform(buffer.name, UniformInt.class);

                        if (uniform != null)
                        {
                            uniform.set(i);

                            i += 1;
                        }
                    }
                }
            });

            this.stages.add(stage);

            previous = shaderStage;
        }
    }

    private Framebuffer setup(List<ShaderBuffer> buffers)
    {
        int colors = 0;

        Framebuffer framebuffer = new Framebuffer();

        framebuffer.deleteTextures().clearCallback(() ->
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
            Texture texture = new Texture();

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

        for (Stage stage : this.stages)
        {
            stage.framebuffer.resize(w, h);
        }
    }

    public static class Stage
    {
        public Shader shader;
        public Framebuffer framebuffer;
        public List<ShaderBuffer> buffers;
    }
}