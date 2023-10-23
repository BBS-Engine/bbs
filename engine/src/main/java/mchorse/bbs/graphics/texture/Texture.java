package mchorse.bbs.graphics.texture;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.opengl.GL11;
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
    private boolean refreshable = true;
    private boolean clearable;

    private TextureFormat format = TextureFormat.RGBA_U8;

    public Texture()
    {
        this.id = GL11.glGenTextures();
        this.target = GL11.GL_TEXTURE_2D;

        this.bind();
    }

    public Texture notRefreshable()
    {
        this.refreshable = false;

        return this;
    }

    public void setClearable(boolean clearable)
    {
        this.clearable = clearable;
    }

    public boolean isClearable()
    {
        return this.clearable;
    }

    public TextureFormat getFormat()
    {
        return this.format;
    }

    public boolean isMipmap()
    {
        return this.mipmap;
    }

    public boolean isRefreshable()
    {
        return this.refreshable;
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
        GLStates.activeTexture(texture);
        GL11.glBindTexture(this.target, this.id);
    }

    public void unbind()
    {
        GL11.glBindTexture(this.target, 0);
    }

    public void unbind(int texture)
    {
        GLStates.activeTexture(texture);
        GL11.glBindTexture(this.target, 0);
    }

    public void setFormat(TextureFormat format)
    {
        this.format = format;
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

        GL11.glTexImage2D(this.target, 0, this.format.internal, width, height, 0, this.format.format, this.format.type, 0);
    }

    public void updateTexture(Pixels pixels)
    {
        this.updateTexture(this.target, pixels);
    }

    public void updateTexture(int target, Pixels pixels)
    {
        this.uploadTexture(target, 0, pixels.width, pixels.height, pixels.getBuffer());
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
        /* Some textures might not be pixel aligned. For example FunkyFight's 398x444 avatar
         * wasn't aligned, and it caused some interesting visual issues when loading
         * the texture. This fixes it.
         *
         * https://www.khronos.org/opengl/wiki/Pixel_Transfer#Pixel_layout */
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        this.setFormat(pixels.bits == 4 ? TextureFormat.RGBA_U8 : TextureFormat.RGB_U8);
        this.uploadTexture(target, level, pixels.width, pixels.height, pixels.getBuffer());

        pixels.delete();
    }

    public void uploadTexture(int target, int level, int w, int h, ByteBuffer buffer)
    {
        GL11.glTexImage2D(target, level, this.format.internal, w, h, 0, this.format.format, this.format.type, buffer);

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