package mchorse.bbs.graphics.ubo;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

public abstract class UBO implements IDisposable
{
    public int id = -1;
    public final int unit;

    public UBO(int unit)
    {
        this.unit = unit;
    }

    public void init()
    {
        this.id = GL15.glGenBuffers();

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.id);
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, this.size(), GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
    }

    protected abstract long size();

    public void bind()
    {
        this.bind(this.unit);
    }

    public void bind(int unit)
    {
        GL31.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, unit, this.id);
    }

    @Override
    public void delete()
    {
        if (this.id != -1)
        {
            GL15.glDeleteBuffers(this.id);

            this.id = -1;
        }
    }
}