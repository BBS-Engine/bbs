package mchorse.bbs.graphics.texture;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.Pixels;

import java.util.HashMap;
import java.util.Map;

public class TextureExtruder
{
    private Map<Link, VAO> extruded = new HashMap<>();

    public void delete(Link key)
    {
        VAO vao = this.extruded.remove(key);

        if (vao != null)
        {
            vao.delete();
        }
    }

    public void deleteAll()
    {
        for (VAO vao : this.extruded.values())
        {
            if (vao != null)
            {
                vao.delete();
            }
        }

        this.extruded.clear();
    }

    public VAO get(Link key)
    {
        if (this.extruded.containsKey(key))
        {
            return this.extruded.get(key);
        }

        Pixels pixels = null;

        try
        {
            pixels = BBS.getTextures().getPixels(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (pixels == null)
        {
            this.extruded.put(key, null);

            return null;
        }

        VAO vao = this.generate(pixels);

        this.extruded.put(key, vao);

        return vao;
    }

    private VAO generate(Pixels pixels)
    {
        VAO vao = new VAO().register(VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        VAOBuilder builder = BBS.getRender().getVAO().setup(vao, null);

        builder.begin();

        float p = 0.5F;
        float n = -0.5F;
        float u1 = 0F;
        float v1 = 0F;
        float u2 = 1F;
        float v2 = 1F;
        float d = 0.5F / 16F;

        Draw.fillTexturedNormalQuad(builder,
            p, n, d,
            n, n, d,
            n, p, d,
            p, p, d,
            u1, v1, u2, v2,
            1F, 1F, 1F, 1F,
            0F, 0F, 1F
        );

        Draw.fillTexturedNormalQuad(builder,
            n, n, -d,
            p, n, -d,
            p, p, -d,
            n, p, -d,
            u2, v1, u1, v2,
            1F, 1F, 1F, 1F,
            0F, 0F, -1F
        );

        for (int i = 0; i < pixels.width; i++)
        {
            for (int j = 0; j < pixels.height; j++)
            {
                if (hasPixel(pixels, i, j))
                {
                    generateNeighbors(pixels, builder, i, j, i, j, d);
                }
            }
        }

        builder.flush();

        return vao;
    }

    private void generateNeighbors(Pixels pixels, VAOBuilder builder, int i, int j, int x, int y, float d)
    {
        float w = pixels.width;
        float h = pixels.height;
        float u = (x + 0.5F) / w;
        float v = (y + 0.5F) / h;

        if (!hasPixel(pixels, x - 1, y) || i == 0)
        {
            Draw.fillTexturedNormalQuad(builder,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                -1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x + 1, y) || i == 15)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x, y - 1) || j == 0)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, 1F, 0F
            );
        }

        if (!hasPixel(pixels, x, y + 1) || j == 15)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, -1F, 0F
            );
        }
    }

    private boolean hasPixel(Pixels pixels, int x, int y)
    {
        Color pixel = pixels.getColor(x, y);

        return pixel != null && pixel.a >= 1;
    }
}