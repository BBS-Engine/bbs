package mchorse.bbs.utils.resources;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Pixels implements IDisposable
{
    private ByteBuffer buffer;
    public final int width;
    public final int height;
    public final int bits;

    public Color color = new Color();

    /**
     * Create pixels object from given PNG stream
     */
    public static Pixels fromPNGStream(InputStream stream) throws IOException
    {
        ByteBuffer image = IOUtils.readByteBuffer(stream, 8 * 1024);
        ByteBuffer pixels;
        int w;
        int h;
        int bitsPerPixel;

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer bits = stack.mallocInt(1);

            if (!STBImage.stbi_info_from_memory(image, width, height, bits))
            {
                throw new RuntimeException("Failed to read image information: " + STBImage.stbi_failure_reason());
            }

            w = width.get(0);
            h = height.get(0);
            bitsPerPixel = bits.get(0);

            pixels = STBImage.stbi_load_from_memory(image, width, height, bits, 0);
        }

        MemoryUtil.memFree(image);

        return new Pixels(pixels, w, h, bitsPerPixel);
    }

    public static Pixels fromTexture(Texture texture)
    {
        if (!texture.isValid())
        {
            return null;
        }

        ByteBuffer buffer = MemoryUtil.memAlloc(texture.width * texture.height * 4);

        texture.bind();
        GL11.glGetTexImage(texture.target, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        texture.unbind();

        return new Pixels(buffer, texture.width, texture.height);
    }

    public static Pixels fromIntArray(int width, int height, int[] data)
    {
        Pixels pixels = fromSize(width, height);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int i = x + y * width;

                pixels.setColor(x, y, Colors.COLOR.set(data[i], true));
            }
        }

        return pixels;
    }

    public static Pixels fromSize(int w, int h)
    {
        ByteBuffer buffer = MemoryUtil.memAlloc(w * h * 4);

        buffer.position(0);

        for (int i = 0, c = w * h; i < c; i++)
        {
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
        }

        return new Pixels(buffer, w, h);
    }

    public Pixels(ByteBuffer buffer, int w, int h)
    {
        this(buffer, w, h, 4);
    }

    public Pixels(ByteBuffer buffer, int w, int h, int bits)
    {
        this.buffer = buffer;
        this.width = w;
        this.height = h;
        this.bits = bits;
    }

    public ByteBuffer getBuffer()
    {
        return this.buffer;
    }

    public int toIndex(int x, int y)
    {
        return x + y * this.width;
    }

    public int toX(int index)
    {
        return index % this.width;
    }

    public int toY(int index)
    {
        return index / this.width;
    }

    public int getCount()
    {
        return this.width * this.height;
    }

    public Color getColor(int index)
    {
        if (index < 0 || index >= this.width * this.height)
        {
            return null;
        }

        this.buffer.position(index * this.bits);
        this.color.r = ((int) this.buffer.get() & 0xff) / 255F;
        this.color.g = ((int) this.buffer.get() & 0xff) / 255F;
        this.color.b = ((int) this.buffer.get() & 0xff) / 255F;
        this.color.a = this.bits == 4 ? ((int) this.buffer.get() & 0xff) / 255F : 1F;

        return this.color;
    }

    public Color getColor(int x, int y)
    {
        return this.getColor(this.toIndex(x, y));
    }

    public void setColor(int index, Color color)
    {
        this.buffer.position(index * this.bits);
        this.buffer.put((byte) (color.r * 0xff));
        this.buffer.put((byte) (color.g * 0xff));
        this.buffer.put((byte) (color.b * 0xff));

        if (this.bits == 4)
        {
            this.buffer.put((byte) (color.a * 0xff));
        }
    }

    public void setColor(int x, int y, Color color)
    {
        this.setColor(this.toIndex(x, y), color);
    }

    public void draw(Pixels pixels, int x, int y)
    {
        Color color = new Color();

        for (int i = Math.max(x, 0), ic = Math.min(x + pixels.width, this.width); i < ic; i++)
        {
            for (int j = Math.max(y, 0), jc = Math.min(y + pixels.height, this.height); j < jc; j++)
            {
                int px = i - x;
                int py = j - y;

                Color target = pixels.getColor(px, py);
                Color source = this.getColor(i, j);

                color.a = 1 - (1 - target.a) * (1 - source.a);
                color.r = target.r * target.a / color.a + source.r * source.a * (1 - target.a) / color.a;
                color.g = target.g * target.a / color.a + source.g * source.a * (1 - target.a) / color.a;
                color.b = target.b * target.a / color.a + source.b * source.a * (1 - target.a) / color.a;

                this.setColor(i, j, color);
            }
        }
    }

    public void draw(Pixels pixels, int x, int y, int w, int h)
    {
        Color color = new Color();

        for (int i = Math.max(x, 0), ic = Math.min(x + w, this.width); i < ic; i++)
        {
            for (int j = Math.max(y, 0), jc = Math.min(y + h, this.height); j < jc; j++)
            {
                float fx = (i - x) / (float) w;
                float fy = (j - y) / (float) h;
                int px = (int) (pixels.width * fx);
                int py = (int) (pixels.height * fy);

                Color target = pixels.getColor(px, py);
                Color source = this.getColor(i, j);

                color.a = 1 - (1 - target.a) * (1 - source.a);
                color.r = target.r * target.a / color.a + source.r * source.a * (1 - target.a) / color.a;
                color.g = target.g * target.a / color.a + source.g * source.a * (1 - target.a) / color.a;
                color.b = target.b * target.a / color.a + source.b * source.a * (1 - target.a) / color.a;

                this.setColor(i, j, color);
            }
        }
    }

    public void drawRect(int x, int y, int w, int h, int c)
    {
        Color color = new Color().set(c);

        for (int i = Math.max(x, 0), ic = Math.min(x + w, this.width); i < ic; i++)
        {
            for (int j = Math.max(y, 0), jc = Math.min(y + h, this.height); j < jc; j++)
            {
                this.setColor(i, j, color);
            }
        }
    }

    public int[] getARGB()
    {
        int[] colors = new int[this.width * this.height * 4];

        for (int i = 0, c = this.getCount(); i < c; i++)
        {
            colors[i] = this.getColor(i).getARGBColor();
        }

        return colors;
    }

    public Pixels createCopy(int x, int y, int w, int h)
    {
        Pixels pixels = fromSize(w, h);

        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < h; j++)
            {
                Color color = this.getColor(x + i, y + j);

                pixels.setColor(i, j, color);
            }
        }

        return pixels;
    }

    public void rewindBuffer()
    {
        if (this.buffer != null)
        {
            this.buffer.position(0);
            this.buffer.limit(this.buffer.capacity());
        }
    }

    @Override
    public void delete()
    {
        MemoryUtil.memFree(this.buffer);

        this.buffer = null;
    }
}