package mchorse.bbs.voxel.generation;

import mchorse.bbs.settings.values.ValueBlockLink;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;

import java.util.Random;

public class GeneratorDefault extends GeneratorFlat
{
    public ValueBlockLink log = new ValueBlockLink("log");
    public ValueBlockLink leaves = new ValueBlockLink("leaves");

    public GeneratorDefault()
    {
        this.group.add(this.log);
        this.group.add(this.leaves);
    }

    /**
     * Generate blocks into the world at given chunk
     */
    @Override
    public void generate(ChunkDisplay display, ChunkManager chunks)
    {
        int xx = display.x;
        int yy = display.y;
        int zz = display.z;
        int fx = MathUtils.toChunk(xx, 64);
        int fz = MathUtils.toChunk(zz, 64);
        int s = chunks.s;

        Random random = new Random();

        this.rand.setSeed(this.seed + fx * 10);
        long a = this.rand.nextLong();
        this.rand.setSeed(this.seed + fz * 10);
        long b = this.rand.nextLong();
        this.rand.setSeed(this.seed + a + b);

        fx = MathUtils.toChunk(xx, 32);
        fz = MathUtils.toChunk(zz, 32);

        IBlockVariant grass = chunks.builder.models.getVariant(this.primary.get());
        IBlockVariant earth = chunks.builder.models.getVariant(this.secondary.get());
        IBlockVariant bush1 = chunks.builder.models.getVariant(this.foliage1.get());
        IBlockVariant bush2 = chunks.builder.models.getVariant(this.foliage2.get());

        for (int x = 0; x < s; x++)
        {
            for (int z = 0; z < s; z++)
            {
                double c00 = this.random(random, this.seed + (xx) * 40 - (zz) * 160);
                double c10 = this.random(random, this.seed + (xx + s) * 40 - (zz) * 160);
                double c01 = this.random(random, this.seed + (xx) * 40 - (zz + s) * 160);
                double c11 = this.random(random, this.seed + (xx + s) * 40 - (zz + s) * 160);

                double a00 = this.random(random, this.seed + (fx) * 40 - (fz) * 100);
                double a10 = this.random(random, this.seed + (fx + 1) * 40 - (fz) * 100);
                double a01 = this.random(random, this.seed + (fx) * 40 - (fz + 1) * 100);
                double a11 = this.random(random, this.seed + (fx + 1) * 40 - (fz + 1) * 100);

                double amplitude = Interpolations.bilerp((x + (xx - fx * 32)) / 32F, (z + (zz - fz * 32)) / 32F, a00, a10, a01, a11) * 48;

                int y = (int) (Interpolations.bilerp(x / (float) s, z / (float) s, c00, c10, c01, c11) * amplitude) - 30;
                int diff = y - yy;

                for (int i = 0; i < s && i < diff; i++)
                {
                    display.chunk.setBlock(x, i, z, y - (yy + i) < 4 ? grass : earth);
                }

                if (this.rand.nextDouble() < 0.005F && diff < (s - 1))
                {
                    this.buildTree(xx + x, y, zz + z, chunks);
                }
                else if (this.rand.nextDouble() < 0.02F && diff < (s - 1))
                {
                    display.chunk.setBlock(x, diff, z, this.rand.nextInt(5) < 3 ? bush1 : bush2);
                }
            }
        }

        display.dirty();
    }

    private double random(Random random, long seed)
    {
        random.setSeed(seed);

        return random.nextDouble();
    }

    private void buildTree(int x, int y, int z, ChunkManager chunks)
    {
        int h = this.rand.nextInt(3) + 4;

        IBlockVariant log = chunks.builder.models.getVariant(this.log.get());
        IBlockVariant leaves = chunks.builder.models.getVariant(this.leaves.get());

        for (int i = 0; i < h; i++)
        {
            chunks.setBlock(x, y + i, z, log);
        }

        for (int i = 0; i < h - 1; i++)
        {
            int rad = i < 2 ? 5 : 3;
            int off = i < 2 ? 2 : 1;

            for (int j = 0; j < rad; j++)
            {
                for (int k = 0; k < rad; k++)
                {
                    if (!chunks.hasBlock(x - off + j, y + i + h - 1, z - off + k))
                    {
                        chunks.setBlock(x - off + j, y + i + h - 1, z - off + k, leaves);
                    }
                }
            }
        }
    }
}