package mchorse.bbs.voxel.processor;

import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;
import org.joml.Vector3i;

public class SmoothProcessor extends Processor
{
    private IBlockVariant fallback;
    private float[][] kernel;

    public static float[][] generateBlurKernel(int radius, float sigma)
    {
        int size = radius * 2 + 1;
        float[][] kernel = new float[size][size];
        float factor = 0.0f;
        float twoSigmaSq = 2 * sigma * sigma;

        for (int i = -radius; i <= radius; i++)
        {
            for (int j = -radius; j <= radius; j++)
            {
                factor += (float) Math.exp(-(i * i + j * j) / twoSigmaSq);
            }
        }

        for (int i = -radius; i <= radius; i++)
        {
            for (int j = -radius; j <= radius; j++)
            {
                kernel[i + radius][j + radius] = (float) Math.exp(-(i * i + j * j) / twoSigmaSq) / factor;
            }
        }

        return kernel;
    }

    public SmoothProcessor(IBlockVariant fallback, float[][] kernel)
    {
        this.fallback = fallback;
        this.kernel = kernel;
    }

    @Override
    public void process(Vector3i min, Vector3i max, ChunkProxy proxy)
    {
        int w = max.x - min.x + 1;
        int d = max.z - min.z + 1;
        int[][] heights = new int[w][d];

        for (int x = min.x; x < max.x + 1; x++)
        {
            for (int z = min.z; z < max.z + 1; z++)
            {
                for (int y = max.y; y >= min.y; y--)
                {
                    IBlockVariant variant = proxy.getBlock(x, y, z);

                    if (!variant.isAir())
                    {
                        heights[x - min.x][z - min.z] = y;

                        break;
                    }
                }
            }
        }

        for (int x = 0; x < w; x++)
        {
            for (int z = 0; z < d; z++)
            {
                float newHeight = 0;

                for (int i = 0; i < this.kernel.length; i++)
                {
                    for (int j = 0; j < this.kernel.length; j++)
                    {
                        int xx = MathUtils.clamp(x + (i - 1), 0, w - 1);
                        int zz = MathUtils.clamp(z + (j - 1), 0, d - 1);

                        newHeight += heights[xx][zz] * this.kernel[i][j];
                    }
                }

                heights[x][z] = Math.round(newHeight);
            }
        }

        for (int x = min.x; x < max.x + 1; x++)
        {
            for (int z = min.z; z < max.z + 1; z++)
            {
                int h = heights[x - min.x][z - min.z];

                for (int y = Math.min(min.y, h); y < Math.max(max.y + 1, h); y++)
                {
                    IBlockVariant current = proxy.getBlock(x, y, z);

                    if (y < h && current.isAir())
                    {
                        proxy.setBlock(x, y, z, this.fallback);
                    }
                    else if (y > h && !current.isAir())
                    {
                        proxy.setBlock(x, y, z, proxy.getAir());
                    }
                }
            }
        }
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {}
}