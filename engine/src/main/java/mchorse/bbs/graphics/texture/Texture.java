package mchorse.bbs.graphics.texture;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * Texture class
 * 
 * This class is responsible for managing a state of a texture
 */
public class Texture implements IDisposable
{
    public int id;
    public int target;

    public int width;
    public int height;

    private boolean mipmap;

    private int internalFormat = GL11.GL_RGBA8;
    private int format = GL11.GL_RGBA;
    private int type = GL11.GL_UNSIGNED_BYTE;

    public Texture()
    {
        this.id = GL11.glGenTextures();
        this.target = GL11.GL_TEXTURE_2D;

        this.bind();
    }

    public boolean isMipmap()
    {
        return this.mipmap;
    }

    public boolean isValid()
    {
        return this.id >= 0;
    }

    public void bind()
    {
        GL11.glBindTexture(this.target, this.id);
    }

    public void bind(int texture)
    {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + texture);
        GL11.glBindTexture(this.target, this.id);
    }

    public void unbind()
    {
        GL11.glBindTexture(this.target, 0);
    }

    public void unbind(int texture)
    {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + texture);
        GL11.glBindTexture(this.target, 0);
    }

    public void setFormat(int internalFormat, int format, int type)
    {
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }

    public int getFilter()
    {
        return this.getParameter(GL11.GL_TEXTURE_MIN_FILTER);
    }

    public int getParameter(int parameter)
    {
        return GL11.glGetTexParameteri(this.target, parameter);
    }

    public void setFilter(int filter)
    {
        this.setParameter(GL11.GL_TEXTURE_MAG_FILTER, filter);
        this.setParameter(GL11.GL_TEXTURE_MIN_FILTER, filter);
    }

    public void setWrap(int mode)
    {
        this.setParameter(GL11.GL_TEXTURE_WRAP_S, mode);
        this.setParameter(GL11.GL_TEXTURE_WRAP_T, mode);
    }

    public void setParameter(int param, int value)
    {
        GL11.glTexParameteri(this.target, param, value);
    }

    @Override
    public void delete()
    {
        GL11.glDeleteTextures(this.id);
        this.id = -1;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;

        GL11.glTexImage2D(this.target, 0, this.internalFormat, width, height, 0, this.format, this.type, 0);
    }

    public void updateTexture(Pixels pixels)
    {
        this.updateTexture(this.target, pixels);
    }

    public void updateTexture(int target, Pixels pixels)
    {
        this.uploadTexture(target, pixels.width, pixels.height, pixels.bits, pixels.getBuffer());
    }

    public void uploadTexture(Pixels pixels)
    {
        this.uploadTexture(this.target, pixels);
    }

    public void uploadTexture(int target, Pixels pixels)
    {
        this.uploadTexture(target, 0, pixels);
    }

    public void uploadTexture(int target, int level, Pixels pixels)
    {
        int internal = pixels.bits == 4 ? GL11.GL_RGBA8 : GL11.GL_RGB8;
        int format = pixels.bits == 4 ? GL11.GL_RGBA : GL11.GL_RGB;

        this.setFormat(internal, format, GL11.GL_UNSIGNED_BYTE);
        this.uploadTexture(target, level, pixels.width, pixels.height, pixels.getBuffer());

        pixels.delete();
    }

    public void uploadTexture(int target, int level, int w, int h, ByteBuffer buffer)
    {
        GL11.glTexImage2D(target, level, this.internalFormat, w, h, 0, this.format, GL11.GL_UNSIGNED_BYTE, buffer);

        if (level == 0)
        {
            this.width = w;
            this.height = h;
        }
    }

    public void generateMipmap()
    {
        this.mipmap = true;

        GL30.glGenerateMipmap(this.target);
    }
}