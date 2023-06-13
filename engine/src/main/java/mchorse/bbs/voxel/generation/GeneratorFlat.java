package mchorse.bbs.voxel.generation;

import mchorse.bbs.settings.values.ValueBlockLink;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;

public class GeneratorFlat extends Generator
{
    public ValueBlockLink primary = new ValueBlockLink("primary");
    public ValueBlockLink secondary = new ValueBlockLink("secondary");
    public ValueBlockLink foliage1 = new ValueBlockLink("foliage1");
    public ValueBlockLink foliage2 = new ValueBlockLink("foliage2");

    public GeneratorFlat()
    {
        this.group.add(this.primary);
        this.group.add(this.secondary);
        this.group.add(this.foliage1);
        this.group.add(this.foliage2);
    }

    @Override
    public void generate(ChunkDisplay display, ChunkManager chunks)
    {
        int xx = display.x;
        int yy = display.y;
        int zz = display.z;

        if (yy > 0)
        {
            return;
        }

        if (xx == 0 && yy == 0 && zz == 0)
        {
            for (int i = 0; i < chunks.builder.models.variants.size(); i++)
            {
                chunks.setBlock(0, 0, i, chunks.builder.models.variants.get(i));
            }
        }

        int fx = MathUtils.toChunk(xx, 64);
        int fz = MathUtils.toChunk(zz, 64);
        int s = chunks.s;

        this.rand.setSeed(this.seed + fx * 10);
        long a = this.rand.nextLong();
        this.rand.setSeed(this.seed + fz * 10);
        long b = this.rand.nextLong();
        this.rand.setSeed(this.seed + a + b);

        IBlockVariant grass = chunks.builder.models.getVariant(this.primary.get());
        IBlockVariant earth = chunks.builder.models.getVariant(this.secondary.get());
        IBlockVariant bush1 = chunks.builder.models.getVariant(this.foliage1.get());
        IBlockVariant bush2 = chunks.builder.models.getVariant(this.foliage2.get());

        for (int x = 0; x < s; x++)
        {
            for (int z = 0; z < s; z++)
            {
                int y = 0;
                int diff = y - yy;

                for (int i = 0; i < s && i < diff; i++)
                {
                    display.chunk.setBlock(x, i, z, y - (yy + i) < 4 ? grass : earth);
                }

                if (this.rand.nextDouble() < 0.02F && diff < (s - 1))
                {
                    display.chunk.setBlock(x, 0, z, this.rand.nextInt(5) < 3 ? bush1 : bush2);
                }
            }
        }

        display.dirty();
    }
}