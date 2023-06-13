package mchorse.bbs.graphics;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.opengl.GL30;

public class Renderbuffer implements IDisposable
{
    public int id;
    public final int target;
    public final int storage;

    public Renderbuffer()
    {
        this(GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_DEPTH24_STENCIL8);
    }

    public Renderbuffer(int target, int storage)
    {
        this.id = GL30.glGenRenderbuffers();
        this.target = target;
        this.storage = storage;
    }

    public void bind()
    {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.id);
    }

    public void unbind()
    {
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
    }

    @Override
    public void delete()
    {
        if (this.id >= 0)
        {
            GL30.glDeleteRenderbuffers(this.id);

            this.id = -1;
        }
    }

    public void resize(int width, int height)
    {
        this.bind();

        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, this.storage, width, height);
    }
}