package mchorse.bbs.graphics.vao;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * VAO class
 * 
 * This class allows to create VAO and related VBOs much easier. Used for 
 * rendering, of course.
 */
public class VAO implements IDisposable
{
    public static final ByteBuffer DATA = MemoryUtil.memAlloc(8 * 1024 * 1024);
    public static final IntBuffer INDICES = MemoryUtil.memAllocInt(2 * 1024 * 1024);

    /**
     * VAO ID
     */
    public int vao;

    /**
     * How many elements to render
     */
    public int count;

    /**
     * Data VBO
     */
    public VBO data;

    /**
     * Index VBO
     */
    public VBO index;

    /**
     * How many triangles does this VAO stores
     */
    public int vertices;

    /**
     * Initialize VAO 
     */
    public VAO()
    {
        this.vao = GL30.glGenVertexArrays();

        this.bind();
    }

    public VAO register(VBOAttributes attributes)
    {
        return this.register(GL15.GL_STATIC_DRAW, attributes);
    }

    public VAO register(int usage, VBOAttributes attributes)
    {
        if (this.data == null)
        {
            this.data = new VBO(usage, attributes);
            this.data.init();
        }

        return this;
    }

    public VAO registerIndex()
    {
        return this.registerIndex(GL15.GL_STATIC_DRAW);
    }

    public VAO registerIndex(int usage)
    {
        this.index = new VBO(usage);
        this.index.init();

        return this;
    }

    /**
     * VAO MUST BE BOUND BEFORE UPLOADING THE DATA!
     */
    public VAO uploadData(ByteBuffer buffer)
    {
        this.bind();
        this.vertices = buffer.limit() / this.data.getBytes();
        this.data.uploadData(buffer);

        return this;
    }

    /**
     * VAO MUST BE BOUND BEFORE UPLOADING THE DATA!
     */
    public VAO uploadIndexData(IntBuffer buffer)
    {
        this.count = buffer.limit();
        this.index.upload(buffer);

        return this;
    }

    /**
     * Bind VAO 
     */
    public void bind()
    {
        GL30.glBindVertexArray(this.vao);
    }

    /**
     * Unbind VAO 
     */
    public void unbind()
    {
        GL30.glBindVertexArray(0);
    }

    /**
     * Bind VAO and its VBOs for rendering
     */
    public void bindForRender()
    {
        this.bind();

        this.data.attributes.bindForRender();

        if (this.index != null)
        {
            this.index.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);
        }
    }

    /**
     * Render (managing shader state is your responsibility)
     */
    public void renderElements()
    {
        this.renderElements(GL11.GL_TRIANGLES);
    }

    public void renderElements(int mode)
    {
        GL11.glDrawElements(mode, this.count, GL11.GL_UNSIGNED_INT, 0);
    }

    public void render(int mode)
    {
        this.render(mode, this.vertices);
    }

    public void render(int mode, int elements)
    {
        GL11.glDrawArrays(mode, 0, elements);
    }

    /**
     * Draw VAO with triangles (assuming that you have filled)
     */
    public void renderTriangles()
    {
        this.renderTriangles(this.vertices);
    }

    public void renderTriangles(int count)
    {
        this.render(GL11.GL_TRIANGLES, count);
    }

    /**
     * Unbind VAO and its VBOs after rendering
     */
    public void unbindForRender()
    {
        if (this.index != null)
        {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        this.data.attributes.unbindForRender();

        this.unbind();
    }

    /**
     * Delete VAO and VBOs
     */
    @Override
    public void delete()
    {
        this.unbindForRender();

        if (this.data != null)
        {
            this.data.delete();
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        if (this.index != null)
        {
            this.index.delete();
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDeleteVertexArrays(this.vao);

        /* Reset state */
        this.data = null;
        this.index = null;
        this.count = 0;
        this.vao = 0;
    }
}