package mchorse.studio;

import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.pipeline.ShaderBuffer;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.pipeline.ShaderStage;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.texture.TextureFormat;
import mchorse.bbs.graphics.vao.VBOAttributes;
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
    public Framebuffer shadow;

    public Map<String, Buffer> textures = new HashMap<>();
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

            this.gbuffer = null;
        }

        if (this.shadow != null)
        {
            this.shadow.delete();

            this.shadow = null;
        }

        for (Stage stage : this.stages)
        {
            stage.framebuffer.delete();
        }

        this.textures.clear();
        this.stages.clear();

        /* Setup */
        List<Texture> gbuffers = this.setupTextures(this.pipeline.gbuffers);
        List<Texture> compositeBuffers = this.setupTextures(this.pipeline.composite);

        this.gbuffer = this.setup(gbuffers);

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
                int i = this.getTextureIndex();

                UniformInt lightmap = shader.getUniform("u_lightmap", UniformInt.class);
                UniformInt shadowmap = shader.getUniform("u_shadowmap", UniformInt.class);

                if (lightmap != null) lightmap.set(0);
                if (shadowmap != null) shadowmap.set(1);

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

        if (this.pipeline.shadowMap)
        {
            Framebuffer framebuffer = new Framebuffer();
            Texture texture = new Texture();

            texture.setFilter(GL11.GL_NEAREST);
            texture.setFormat(TextureFormat.DEPTH_F24);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);

            framebuffer.attach(texture, GL30.GL_DEPTH_ATTACHMENT);
            framebuffer.resize(this.pipeline.shadowResolution, this.pipeline.shadowResolution);

            this.shadow = framebuffer;
        }
    }

    public int getTextureIndex()
    {
        return this.pipeline.shadowMap ? 2 : 1;
    }

    private List<Texture> setupTextures(List<ShaderBuffer> buffers)
    {
        List<Texture> textures = new ArrayList<>();

        for (ShaderBuffer buffer : buffers)
        {
            Texture texture = new Texture();

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