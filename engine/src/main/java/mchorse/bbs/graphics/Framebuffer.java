package mchorse.bbs.graphics;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAOBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Framebuffer implements IDisposable
{
    public int id;
    public List<Texture> textures = new ArrayList<Texture>();
    public final List<Renderbuffer> renderbuffers = new ArrayList<Renderbuffer>();

    private boolean deleteTextures;

    public static void renderToQuad(RenderingContext context, Shader shader)
    {
        /* Requires VBOAttributes.VERTEX_2D shader */
        VAOBuilder builder = context.getVAO().setup(shader);

        builder.begin();
        builder.xy(-1, -1).xy(1, 1).xy(-1, 1);
        builder.xy(1, -1).xy(1, 1).xy(-1, -1);
        builder.render();
    }

    public Framebuffer()
    {
        this.id = GL30.glGenFramebuffers();
    }

    public Framebuffer deleteTextures()
    {
        this.deleteTextures = true;

        return this;
    }

    public Texture getMainTexture()
    {
        return this.textures.get(0);
    }

    /**
     * Attach a texture as one of the attachment buffers
     */
    public Framebuffer attach(Texture texture, int attachment)
    {
        this.textures.add(texture);

        this.bind();
        texture.bind();

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, texture.target, texture.id, 0);

        return this;
    }

    /**
     * Attach a renderbuffer as one of the attachment buffers
     */
    public void attach(Renderbuffer renderbuffer)
    {
        this.renderbuffers.add(renderbuffer);
        renderbuffer.bind();

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, renderbuffer.target, GL30.GL_RENDERBUFFER, renderbuffer.id);
    }

    public boolean isComplete()
    {
        return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    public void apply()
    {
        Texture texture = this.getMainTexture();

        GL11.glViewport(0, 0, texture.width, texture.height);
        this.bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void bind()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.id);
    }

    public void unbind()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void resize(int w, int h)
    {
        for (Texture texture : this.textures)
        {
            texture.bind();
            texture.setSize(w, h);
        }

        for (Renderbuffer renderbuffer : this.renderbuffers)
        {
            renderbuffer.bind();
            renderbuffer.resize(w, h);
            renderbuffer.unbind();
        }
    }

    @Override
    public void delete()
    {
        GL30.glDeleteFramebuffers(this.id);

        if (this.deleteTextures)
        {
            for (Texture texture : this.textures)
            {
                texture.delete();
            }

            this.textures.clear();
        }

        for (Renderbuffer renderbuffer : this.renderbuffers)
        {
            renderbuffer.delete();
        }

        this.renderbuffers.clear();
    }
}