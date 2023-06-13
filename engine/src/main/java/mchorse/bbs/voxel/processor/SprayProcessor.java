package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class SprayProcessor extends Processor
{
    public IBlockVariant block;
    public float chance;

    public SprayProcessor(IBlockVariant block, float chance)
    {
        this.block = block;
        this.chance = chance;
    }

    @Override
    protected boolean isYReversed()
    {
        return true;
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        IBlockVariant under = proxy.getBlock(x, y - 1, z);
        IBlockVariant block = proxy.getBlock(x, y, z);

        if (Math.random() < this.chance && block.isAir())
        {
            if (proxy.isMaskEnabled())
            {
                if (proxy.isBlockMasked(under))
                {
                    proxy.setBlockUnmasked(x, y, z, this.block);
                }
            }
            else if (!under.isAir())
            {
                proxy.setBlock(x, y, z, this.block);
            }
        }
    }
}