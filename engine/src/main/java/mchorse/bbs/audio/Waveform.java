package mchorse.bbs.audio;

import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

public class Waveform
{
    public float[] average;
    public float[] maximum;

    private List<Texture> sprites = new ArrayList<Texture>();
    private int w;
    private int h;
    private int pixelsPerSecond;

    public void generate(Wave data, int pixelsPerSecond, int height)
    {
        if (data.getBytesPerSample() != 2)
        {
            throw new IllegalStateException("Waveform generation doesn't support non 16-bit audio data!");
        }

        this.populate(data, pixelsPerSecond, height);
        this.render();
    }

    public void render()
    {
        this.delete();

        int maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE) / 2;
        int count = (int) Math.ceil(this.w / (double) maxTextureSize);
        int offset = 0;

        for (int t = 0; t < count; t++)
        {
            Texture texture = new Texture();
            int width = Math.min(this.w - offset, maxTextureSize);

            Pixels pixels = Pixels.fromSize(width, this.h);

            for (int i = offset, j = 0, c = Math.min(offset + width, this.average.length); i < c; i++, j++)
            {
                float average = this.average[i];
                float maximum = this.maximum[i];

                int maxHeight = (int) (maximum * this.h);
                int avgHeight = (int) (average * (this.h - 1)) + 1;

                if (avgHeight > 0)
                {
                    pixels.drawRect(j, this.h / 2 - maxHeight / 2, 1, maxHeight, Colors.WHITE);
                    pixels.drawRect(j, this.h / 2 - avgHeight / 2, 1, avgHeight, Colors.LIGHTEST_GRAY);
                }
            }

            pixels.rewindBuffer();

            texture.bind();
            texture.uploadTexture(pixels);
            texture.setFilter(GL11.GL_NEAREST);
            texture.setParameter(GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            texture.unbind();

            this.sprites.add(texture);

            offset += maxTextureSize;
        }
    }

    public void populate(Wave data, int pixelsPerSecond, int height)
    {
        this.pixelsPerSecond = pixelsPerSecond;
        this.w = (int) (data.getDuration() * pixelsPerSecond);
        this.h = height;
        this.average = new float[this.w];
        this.maximum = new float[this.w];

        int region = data.getScanRegion(pixelsPerSecond);

        for (int i = 0; i < this.w; i ++)
        {
            int offset = i * region;
            int count = 0;
            float average = 0;
            float maximum = 0;

            for (int j = 0; j < region; j += 2 * data.numChannels)
            {
                if (offset + j + 1 >= data.data.length)
                {
                    break;
                }

                byte a = data.data[offset + j];
                byte b = data.data[offset + j + 1];
                float sample = a + (b << 8);

                maximum = Math.max(maximum, Math.abs(sample));
                average += Math.abs(sample);
                count++;
            }

            average /= count;
            average /= 0xffff / 2;
            maximum /= 0xffff / 2;

            this.average[i] = average;
            this.maximum[i] = maximum;
        }
    }

    public void delete()
    {
        for (Texture sprite : this.sprites)
        {
            sprite.delete();
        }

        this.sprites.clear();
    }

    public boolean isCreated()
    {
        return !this.sprites.isEmpty();
    }

    public int getPixelsPerSecond()
    {
        return this.pixelsPerSecond;
    }

    public int getWidth()
    {
        return this.w;
    }

    public int getHeight()
    {
        return this.h;
    }

    public List<Texture> getSprites()
    {
        return this.sprites;
    }

    /**
     * Draw the waveform out of multiple sprites of desired cropped region
     */
    public void render(Batcher2D batcher, int color, int x, int y, int w, int h, float startTime, float endTime)
    {
        float offset = 0;

        for (Texture sprite : this.sprites)
        {
            float spriteTime = sprite.width / (float) this.pixelsPerSecond;
            float spriteStart = offset;
            float spriteEnd = offset + spriteTime;

            if (spriteStart > endTime)
            {
                break;
            }

            int u1 = (int) ((startTime - spriteStart) * this.pixelsPerSecond);
            int u2 = (int) ((endTime - spriteStart) * this.pixelsPerSecond);
            int w2 = w;

            if (endTime > spriteEnd)
            {
                w2 = (int) (w2 * (spriteEnd / endTime));
            }

            batcher.texturedBox(sprite, color, x, y, w2, h, u1, 0, u2, sprite.height, sprite.width, sprite.height);

            offset = spriteEnd;
            x += u2 - u1;
        }
    }
}