package mchorse.bbs.graphics.vao;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class VBO implements IDisposable
{
    public int id = -1;
    public int usage;

    public VBOAttributes attributes = VBOAttributes.EMPTY;

    public VBO(int usage)
    {
        this.usage = usage;
    }

    public VBO(int usage, VBOAttributes attributes)
    {
        this.usage = usage;
        this.attributes = attributes;
    }

    public int getBytes()
    {
        int bytes = 0;

        for (VBOAttribute attribute : this.attributes.elements)
        {
            bytes += attribute.getBytes();
        }

        return bytes;
    }

    public void uploadData(ByteBuffer buffer)
    {
        this.bind(GL15.GL_ARRAY_BUFFER);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, this.usage);

        int bytes = this.getBytes();
        int offset = 0;

        List<VBOAttribute> attributes = this.attributes.elements;

        for (int i = 0; i < attributes.size(); i++)
        {
            VBOAttribute attribute = attributes.get(i);

            GL20.glVertexAttribPointer(i, attribute.size, attribute.type, attribute.normalized, bytes, offset);
            offset += attribute.getBytes();
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void upload(IntBuffer buffer)
    {
        this.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, this.usage);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void bind(int target)
    {
        if (this.id != -1)
        {
            GL15.glBindBuffer(target, this.id);
        }
    }

    public void init()
    {
        if (this.id == -1)
        {
            this.id = GL15.glGenBuffers();
        }
    }

    @Override
    public void delete()
    {
        this.attributes = VBOAttributes.EMPTY;

        if (this.id != -1)
        {
            GL15.glDeleteBuffers(this.id);

            this.id = -1;
        }
    }
}