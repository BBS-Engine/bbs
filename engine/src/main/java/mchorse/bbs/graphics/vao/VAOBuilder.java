package mchorse.bbs.graphics.vao;

import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.joml.Vectors;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VAOBuilder
{
    private static final MatrixStack tempStack = new MatrixStack();

    public VAO vao;

    private ByteBuffer buffer = VAO.DATA;
    private IntBuffer indices;
    private Shader shader;
    private MatrixStack stack;

    private boolean uploading;

    public final VAOManager vaos;

    public VAOBuilder(VAOManager vaos)
    {
        this.vaos = vaos;
    }

    /* For rendering */

    public VAOBuilder setup(Shader shader)
    {
        return this.setup(shader.attributes, null).shader(shader);
    }

    public VAOBuilder setup(Shader shader, IntBuffer indices)
    {
        return this.setup(shader.attributes, indices).shader(shader);
    }

    /* For data uploading */

    public VAOBuilder setup(VBOAttributes type)
    {
        return this.setup(type, null);
    }

    public VAOBuilder setup(VBOAttributes type, IntBuffer indices)
    {
        return this.setup(this.vaos.getTemporary(type, indices != null), indices);
    }

    public VAOBuilder setup(VAO vao, IntBuffer indices)
    {
        this.vao = vao;
        this.indices = indices;

        return this;
    }

    public VAOBuilder shader(Shader shader)
    {
        this.shader = shader;

        return this;
    }

    public VAOBuilder stack(MatrixStack stack)
    {
        this.stack = stack;

        return this;
    }

    /* Building methods */

    public boolean hasIndex()
    {
        return this.indices != null;
    }

    public VAOBuilder xy(float x, float y)
    {
        Vector4f vector = Vectors.TEMP_4F.set(x, y, 0, 1);

        if (this.stack != null)
        {
            this.stack.getModelMatrix().transform(vector);
        }

        this.buffer.putFloat(vector.x);
        this.buffer.putFloat(vector.y);

        return this;
    }

    public VAOBuilder xyz(float x, float y, float z)
    {
        Vector4f vector = Vectors.TEMP_4F.set(x, y, z, 1);

        if (this.stack != null)
        {
            this.stack.getModelMatrix().transform(vector);
        }

        this.buffer.putFloat(vector.x);
        this.buffer.putFloat(vector.y);
        this.buffer.putFloat(vector.z);

        return this;
    }

    public VAOBuilder xyzw(float x, float y, float z, float w)
    {
        Vector4f vector = Vectors.TEMP_4F.set(x, y, z, w);

        if (this.stack != null)
        {
            this.stack.getModelMatrix().transform(vector);
        }

        this.buffer.putFloat(vector.x);
        this.buffer.putFloat(vector.y);
        this.buffer.putFloat(vector.z);
        this.buffer.putFloat(vector.w);

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
        this.stack = tempStack;

        this.stack.identity();
        this.stack.translate(x, y, z);

        this.begin();
    }

    public void begin()
    {
        if (this.uploading)
        {
            System.err.println("VAOBuilder is already uploading!");
        }

        this.buffer.clear();

        if (this.indices != null)
        {
            this.indices.clear();
        }

        this.uploading = true;
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
        this.buffer.flip();

        if (this.indices != null)
        {
            this.indices.flip();
        }

        this.vao.bind();
        this.vao.uploadData(this.buffer);
        this.buffer.clear();

        if (this.indices != null)
        {
            this.vao.uploadIndexData(this.indices);
        }

        this.reset();
    }

    public void reset()
    {
        this.shader = null;
        this.stack = null;
        this.uploading = false;
    }
}