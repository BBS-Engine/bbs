package mchorse.bbs.graphics.vao;

import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.utils.colors.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VAOBuilder
{
    public VAO vao;
    public ByteBuffer buffer;
    public IntBuffer indices;
    public Shader shader;
    public VBOAttributes enforce;

    public final VAOManager vaos;

    public Vector3f translation = new Vector3f();

    public VAOBuilder(VAOManager vaos)
    {
        this.vaos = vaos;
    }

    public VAOBuilder setup(Shader shader)
    {
        return this.setup(shader.attributes, VAO.DATA).shader(shader);
    }

    public VAOBuilder setup(Shader shader, ByteBuffer buffer)
    {
        return this.setup(shader.attributes, buffer, null).shader(shader);
    }

    public VAOBuilder setup(Shader shader, ByteBuffer buffer, IntBuffer indices)
    {
        return this.setup(shader.attributes, buffer, indices).shader(shader);
    }

    public VAOBuilder setup(VBOAttributes type)
    {
        return this.setup(type, VAO.DATA);
    }

    public VAOBuilder setup(VBOAttributes type, ByteBuffer buffer)
    {
        return this.setup(type, buffer, null);
    }

    public VAOBuilder setup(VBOAttributes type, ByteBuffer buffer, IntBuffer indices)
    {
        return this.setup(this.vaos.getTemporary(type, indices != null), buffer, indices);
    }

    public VAOBuilder setup(VAO vao, ByteBuffer buffer, IntBuffer indices)
    {
        this.vao = vao;
        this.buffer = buffer;
        this.indices = indices;

        return this;
    }

    public VAOBuilder shader(Shader shader)
    {
        this.shader = shader;

        return this;
    }

    public VAOBuilder enforce()
    {
        return this.enforce(this.shader.attributes);
    }

    public VAOBuilder enforce(VBOAttributes attributes)
    {
        this.enforce = attributes;

        return this;
    }

    /* Building methods */

    public boolean hasIndex()
    {
        return this.indices != null;
    }

    public VAOBuilder xy(float x, float y)
    {
        this.buffer.putFloat(x + this.translation.x);
        this.buffer.putFloat(y + this.translation.y);

        return this;
    }

    public VAOBuilder xyz(float x, float y, float z)
    {
        this.buffer.putFloat(x + this.translation.x);
        this.buffer.putFloat(y + this.translation.y);
        this.buffer.putFloat(z + this.translation.z);

        return this;
    }

    public VAOBuilder xyzw(float x, float y, float z, float w)
    {
        this.buffer.putFloat(x + this.translation.x);
        this.buffer.putFloat(y + this.translation.y);
        this.buffer.putFloat(z + this.translation.z);
        this.buffer.putFloat(w);

        return this;
    }

    public VAOBuilder normal(float x, float y, float z)
    {
        this.buffer.putFloat(x);
        this.buffer.putFloat(y);
        this.buffer.putFloat(z);

        return this;
    }

    public VAOBuilder rgb(Color color)
    {
        return this.rgb(color.r, color.g, color.b);
    }

    public VAOBuilder rgb(float r, float g, float b)
    {
        this.buffer.put((byte) (r * 255));
        this.buffer.put((byte) (g * 255));
        this.buffer.put((byte) (b * 255));

        return this;
    }

    public VAOBuilder rgba(Color color)
    {
        return this.rgba(color.r, color.g, color.b, color.a);
    }

    public VAOBuilder rgba(float r, float g, float b, float a)
    {
        this.buffer.put((byte) (r * 255));
        this.buffer.put((byte) (g * 255));
        this.buffer.put((byte) (b * 255));
        this.buffer.put((byte) (a * 255));

        return this;
    }

    public VAOBuilder uv(float u, float v)
    {
        return this.uv(u, v, 1, 1);
    }

    public VAOBuilder uv(float u, float v, float tw, float th)
    {
        this.buffer.putFloat(u / tw);
        this.buffer.putFloat(v / th);

        return this;
    }

    public VAOBuilder index(int index)
    {
        this.indices.put(index);

        return this;
    }

    /* Pipeline methods */

    public void begin(float x, float y, float z)
    {
        this.translation.set(x, y, z);

        this.begin();
    }

    public void begin()
    {
        this.buffer.clear();

        if (this.indices != null)
        {
            this.indices.clear();
        }

        if (this.shader != null && this.enforce != null)
        {
            Shader program = this.shader;

            if (program.attributes != this.enforce)
            {
                throw new RuntimeException("Shader " + program.name.toString() + " is not matching enforced " + this.enforce.name.toString() + " VBO attributes layout! Actual: " + program.attributes.name.toString());
            }
        }
    }

    public void render()
    {
        this.render(GL11.GL_TRIANGLES);
    }

    public void render(int mode)
    {
        if (this.shader != null)
        {
            this.shader.bind();
        }

        this.flush();

        this.vao.bindForRender();

        if (this.indices == null)
        {
            this.vao.render(mode);
        }
        else
        {
            this.vao.renderElements(mode);
        }

        this.vao.unbindForRender();
    }

    public void flush()
    {
        this.shader = null;
        this.enforce = null;

        this.translation.set(0, 0, 0);
        this.buffer.flip();

        if (this.indices != null)
        {
            this.indices.flip();
        }

        this.vao.bind();
        this.vao.uploadData(this.buffer);

        if (this.indices != null)
        {
            this.vao.uploadIndexData(this.indices);
        }
    }
}