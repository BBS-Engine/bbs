package mchorse.bbs.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.Pixels;
import org.joml.Vector2i;

public class ItemExtruder
{
    public static VAO extrude(Pixels pixels, Vector2i uv)
    {
        VAO vao = new VAO().register(VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        VAOBuilder builder = BBS.getRender().getVAO().setup(vao, VAO.DATA, null);

        builder.begin();

        float p = 0.5F;
        float n = -0.5F;
        float u1 = uv.x / (float) pixels.width;
        float v1 = uv.y / (float) pixels.height;
        float u2 = (uv.x + 16F) / (float) pixels.width;
        float v2 = (uv.y + 16F) / (float) pixels.height;
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

        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                int x = uv.x + i;
                int y = uv.y + j;

                if (hasPixel(pixels, x, y))
                {
                    generateNeighbors(pixels, builder, i, j, x, y, d);
                }
            }
        }

        builder.flush();

        return vao;
    }

    private static void generateNeighbors(Pixels pixels, VAOBuilder builder, int i, int j, int x, int y, float d)
    {
        float u = (x + 0.5F) / pixels.width;
        float v = (y + 0.5F) / pixels.height;

        if (!hasPixel(pixels, x - 1, y) || i == 0)
        {
            Draw.fillTexturedNormalQuad(builder,
                i / 16F - 0.5F, -(j + 1) / 16F + 0.5F, -d,
                i / 16F - 0.5F, -j / 16F + 0.5F, -d,
                i / 16F - 0.5F, -j / 16F + 0.5F, d,
                i / 16F - 0.5F, -(j + 1) / 16F + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                -1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x + 1, y) || i == 15)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / 16F - 0.5F, -(j + 1) / 16F + 0.5F, d,
                (i + 1) / 16F - 0.5F, -j / 16F + 0.5F, d,
                (i + 1) / 16F - 0.5F, -j / 16F + 0.5F, -d,
                (i + 1) / 16F - 0.5F, -(j + 1) / 16F + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                1F, 0F, 0F
            );
        }

        if (!hasPixel(pixels, x, y - 1) || j == 0)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / 16F - 0.5F, -j / 16F + 0.5F, d,
                i / 16F - 0.5F, -j / 16F + 0.5F, d,
                i / 16F - 0.5F, -j / 16F + 0.5F, -d,
                (i + 1) / 16F - 0.5F, -j / 16F + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, 1F, 0F
            );
        }

        if (!hasPixel(pixels, x, y + 1) || j == 15)
        {
            Draw.fillTexturedNormalQuad(builder,
                (i + 1) / 16F - 0.5F, -(j + 1) / 16F + 0.5F, -d,
                i / 16F - 0.5F, -(j + 1) / 16F + 0.5F, -d,
                i / 16F - 0.5F, -(j + 1) / 16F + 0.5F, d,
                (i + 1) / 16F - 0.5F, -(j + 1) / 16F + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, -1F, 0F
            );
        }
    }

    private static boolean hasPixel(Pixels pixels, int x, int y)
    {
        Color pixel = pixels.getColor(x, y);

        return pixel != null && pixel.a >= 1;
    }
}