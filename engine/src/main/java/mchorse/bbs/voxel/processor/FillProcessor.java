package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class FillProcessor extends Processor
{
    public IBlockVariant block;
    public boolean hollow;

    public FillProcessor(IBlockVariant block, boolean hollow)
    {
        this.block = block;
        this.hollow = hollow;
    }

    protected double radius(int x, int min, int size)
    {
        return ((x - min + 0.5D) / size - 0.5D) * 2;
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        int dx = x - this.min.x;
        int dy = y - this.min.y;
        int dz = z - this.min.z;

        if (!this.hollow || (dx == 0 || dy == 0 || dz == 0 || dx == this.volume.x - 1 || dy == this.volume.y - 1 || dz == this.volume.z - 1))
        {
            proxy.setBlock(x, y, z, this.block);
        }
    }
}