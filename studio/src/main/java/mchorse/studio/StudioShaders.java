package mchorse.studio;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.pipeline.ShaderBuffer;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.pipeline.ShaderStage;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.texture.TextureFormat;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudioShaders
{
    private ShaderPipeline pipeline;

    public Framebuffer gbuffer;

    public Map<String, Buffer> textures = new HashMap<>();
    public List<Stage> stages = new ArrayList<>();

    public StudioShaders(ShaderPipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public void reload()
    {
        /* Clean up */
        if (this.gbuffer != null) this.gbuffer.delete();

        for (Stage stage : this.stages)
        {
            stage.framebuffer.delete();
        }

        this.textures.clear();
        this.stages.clear();

        /* Setup */
        List<Texture> gbuffers = this.setupTextures(this.pipeline.gbuffers, "g_");

        this.gbuffer = this.setup(gbuffers);

        List<Texture> compositeBuffers = this.setupTextures(this.pipeline.composite, "c_");

        for (int i = 0; i < compositeBuffers.size(); i++)
        {
            Texture texture = compositeBuffers.get(i);
            ShaderBuffer buffer = this.pipeline.composite.get(i);

            this.textures.put(buffer.name, new Buffer(texture, buffer));
        }

        for (ShaderStage shaderStage : this.pipeline.stages)
        {
            List<Texture> output = new ArrayList<>();

            for (String outputTexture : shaderStage.output)
            {
                output.add(this.textures.get(outputTexture).texture);
            }

            Shader stageShader = new Shader(shaderStage.shader, VBOAttributes.VERTEX_2D);
            Framebuffer framebuffer = this.setup(output);

            stageShader.onInitialize((shader) ->
            {
                int i = 1;

                UniformInt lightmap = shader.getUniform("u_lightmap", UniformInt.class);

                if (lightmap != null) lightmap.set(0);

                for (ShaderBuffer buffer : this.pipeline.gbuffers)
                {
                    UniformInt uniform = shader.getUniform(buffer.name, UniformInt.class);

                    if (uniform != null) uniform.set(i++);
                }

                for (String bufferName : shaderStage.input)
                {
                    UniformInt uniform = shader.getUniform(bufferName, UniformInt.class);

                    if (uniform != null) uniform.set(i++);
                }
            });

            List<Texture> inputs = new ArrayList<>();

            for (String input : shaderStage.input)
            {
                inputs.add(this.textures.get(input).texture);
            }

            this.stages.add(new Stage(stageShader, framebuffer, inputs));
        }
    }

    private List<Texture> setupTextures(List<ShaderBuffer> buffers, String prefix)
    {
        List<Texture> textures = new ArrayList<>();

        for (ShaderBuffer buffer : buffers)
        {
            Texture texture = BBS.getTextures().createTexture(new Link("bbs", prefix + buffer.name));

            texture.bind();
            texture.setClearable(buffer.clear);
            texture.setFilter(buffer.linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);
            texture.setFormat(buffer.format);

            textures.add(texture);
        }

        return textures;
    }

    private Framebuffer setup(List<Texture> textures)
    {
        int colors = 0;

        Framebuffer framebuffer = new Framebuffer().enableAdvancedClearing();

        for (Texture texture : textures)
        {
            TextureFormat format = texture.getFormat();

            framebuffer.attach(texture, format.attachment + (format.isColor() ? colors : 0));

            if (format.isColor())
            {
                colors += 1;
            }
        }

        framebuffer.attachments(colors);

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
        public List<Texture> inputs;

        public Stage(Shader shader, Framebuffer framebuffer, List<Texture> inputs)
        {
            this.shader = shader;
            this.framebuffer = framebuffer;
            this.inputs = inputs;
        }
    }

    public static class Buffer
    {
        public Texture texture;
        public ShaderBuffer buffer;

        public Buffer(Texture texture, ShaderBuffer buffer)
        {
            this.texture = texture;
            this.buffer = buffer;
        }
    }
}