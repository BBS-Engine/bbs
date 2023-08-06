package mchorse.bbs.graphics.vao;

import mchorse.bbs.resources.Link;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

public class VBOAttributes
{
    public static final VBOAttributes EMPTY = new VBOAttributes(Link.bbs("empty"));

    public static final VBOAttributes VERTEX = new VBOAttributes(Link.bbs("vertex"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT));

    public static final VBOAttributes VERTEX_RGBA = new VBOAttributes(Link.bbs("vertex_rgba"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public static final VBOAttributes VERTEX_UV_RGBA = new VBOAttributes(Link.bbs("vertex_uv_rgba"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public static final VBOAttributes VERTEX_NORMAL_UV_RGBA = new VBOAttributes(Link.bbs("vertex_normal_uv_rgba"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public static final VBOAttributes VERTEX_NORMAL_UV_LIGHT_RGBA = new VBOAttributes(Link.bbs("vertex_normal_uv_light_rgba"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public static final VBOAttributes VERTEX_NORMAL_UV_RGBA_BONES = new VBOAttributes(Link.bbs("vertex_normal_uv_rgba_bones"))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(3, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true))
        .register(new VBOAttribute(4, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_FLOAT));

    public static final VBOAttributes VERTEX_2D = new VBOAttributes(Link.bbs("vertex_2d"))
        .register(new VBOAttribute(2, GL11.GL_FLOAT));

    public static final VBOAttributes VERTEX_RGBA_2D = new VBOAttributes(Link.bbs("vertex_rgba_2d"))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public static final VBOAttributes VERTEX_UV_RGBA_2D = new VBOAttributes(Link.bbs("vertex_uv_rgba_2d"))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(2, GL11.GL_FLOAT))
        .register(new VBOAttribute(4, GL11.GL_UNSIGNED_BYTE, true));

    public final Link name;
    public List<VBOAttribute> elements = new ArrayList<>();

    public VBOAttributes(Link name)
    {
        this.name = name;
    }

    public VBOAttributes register(VBOAttribute attribute)
    {
        this.elements.add(attribute);

        return this;
    }

    public void bindForRender()
    {
        for (int i = 0; i < this.elements.size(); i++)
        {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    public void unbindForRender()
    {
        for (int i = 0; i < this.elements.size(); i++)
        {
            GL20.glDisableVertexAttribArray(i);
        }
    }
}