package mchorse.bbs.graphics.vao;

import org.lwjgl.opengl.GL11;

public class VBOAttribute
{
    public int size;
    public int type;
    public boolean normalized;

    public VBOAttribute(int size, int type)
    {
        this.size = size;
        this.type = type;
    }

    public VBOAttribute(int size, int type, boolean normalized)
    {
        this.size = size;
        this.type = type;
        this.normalized = normalized;
    }

    public int getBytes()
    {
        return this.size * this.getTypeBytes();
    }

    private int getTypeBytes()
    {
        switch (this.type)
        {
            case GL11.GL_INT:
            case GL11.GL_UNSIGNED_INT:
            case GL11.GL_FLOAT:
                return 4;

            case GL11.GL_SHORT:
            case GL11.GL_UNSIGNED_SHORT:
                return 2;

            case GL11.GL_BYTE:
            case GL11.GL_UNSIGNED_BYTE:
                return 1;
        }

        return 1;
    }
}