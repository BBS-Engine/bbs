package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;

public abstract class Processor
{
    protected Vector3i min;
    protected Vector3i max;

    protected Vector3i volume = new Vector3i();

    protected boolean collect;
    protected Set<Vector3i> placed;

    public Processor collect()
    {
        this.collect = true;
        this.placed = new HashSet<Vector3i>();

        return this;
    }

    public Set<Vector3i> getPlaced()
    {
        return this.placed;
    }

    protected boolean isYReversed()
    {
        return false;
    }

    public void process(Vector3i min, Vector3i max, ChunkProxy proxy)
    {
        this.min = min;
        this.max = max;
        this.volume.set(max).sub(min).add(1, 1, 1);

        for (int x = min.x; x <= max.x; x++)
        {
            for (int y = min.y; y <= max.y; y++)
            {
                int ny = this.isYReversed() ? max.y - (y - min.y) : y;

                for (int z = min.z; z <= max.z; z++)
                {
                    IBlockVariant block = proxy.getAir();

                    if (this.collect)
                    {
                        block = proxy.getBlock(x, ny, z);
                    }

                    this.processBlock(x, ny, z, proxy);

                    if (this.collect && proxy.getBlock(x, ny, z) != block)
                    {
                        this.placed.add(new Vector3i(x, ny, z));
                    }
                }
            }
        }
    }

    protected abstract void processBlock(int x, int y, int z, ChunkProxy proxy);
}