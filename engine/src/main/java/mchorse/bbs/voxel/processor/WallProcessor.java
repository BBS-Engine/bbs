package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class WallProcessor extends Processor
{
    public IBlockVariant block;

    public WallProcessor(IBlockVariant block)
    {
        this.block = block;
    }

    protected double radius(int x, int min, int size)
    {
        return ((x - min + 0.5D) / size - 0.5D) * 2;
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        int dx = x - this.min.x;
        int dz = z - this.min.z;

        if (dx == 0 || dz == 0 || dx == this.volume.x - 1 || dz == this.volume.z - 1)
        {
            proxy.setBlock(x, y, z, this.block);
        }
    }
}