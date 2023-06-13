package mchorse.bbs.voxel;

import mchorse.bbs.voxel.blocks.IBlockVariant;

public interface IBlockAccessor
{
    public boolean hasBlock(int x, int y, int z);

    public IBlockVariant getBlock(int x, int y, int z);
}