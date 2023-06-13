package mchorse.bbs.ui.world.tools.schematic;

import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.BlockSet;
import net.querz.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Schematic class that is responsible for reading schematic data from NBT,
 * and replacing
 */
public class Schematic
{
    private Chunk chunk;
    private List<Integer> data;

    public static int toIndex(int x, int y, int z, int w, int d)
    {
        return (y * d + z) * w + x;
    }

    public static Schematic fromSchematic(CompoundTag schematic, BlockSet blockSet)
    {
        byte[] blocks = schematic.getByteArrayTag("Blocks").getValue();
        byte[] data = schematic.getByteArrayTag("Data").getValue();
        int w = schematic.getShort("Width");
        int h = schematic.getShort("Height");
        int d = schematic.getShort("Length");
        IBlockVariant defaultVariant = blockSet.variants.get(0);
        Chunk chunk = new Chunk(w, h, d, blockSet.air);
        List<Integer> schematicData = new ArrayList<Integer>();

        for (int i = 0, c = w * h * d; i < c; i++)
        {
            schematicData.add(0);
        }

        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                for (int z = 0; z < d; z++)
                {
                    int index = toIndex(x, y, z, w, d);
                    int block = blocks[index] & 0xff;
                    Integer pair = block * 255 + (data[index] & 0xff);

                    if (block != 0)
                    {
                        chunk.setBlock(x, y, z, defaultVariant);
                    }

                    schematicData.set(index, pair);
                }
            }
        }

        return new Schematic(chunk, schematicData);
    }

    public Schematic(Chunk chunk, List<Integer> data)
    {
        this.chunk = chunk;
        this.data = data;
    }

    public Chunk getChunk()
    {
        return this.chunk;
    }

    public Set<Integer> getUniqueBlocks()
    {
        Set<Integer> unique = new LinkedHashSet<Integer>();

        for (Integer integer : this.data)
        {
            if (integer != 0)
            {
                unique.add(integer);
            }
        }

        return unique;
    }

    public int getDataBlockAt(int x, int y, int z)
    {
        if (this.chunk.isOutside(x, y, z))
        {
            return 0;
        }

        return this.data.get(toIndex(x, y, z, this.chunk.w, this.chunk.d));
    }

    public void replace(Integer pair, IBlockVariant b)
    {
        int w = this.chunk.w;
        int h = this.chunk.h;
        int d = this.chunk.d;

        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                for (int z = 0; z < d; z++)
                {
                    Integer block = this.data.get(toIndex(x, y, z, w, d));

                    if (block.equals(pair))
                    {
                        this.chunk.setBlock(x, y, z, b);
                    }
                }
            }
        }
    }
}