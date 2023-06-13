package mchorse.bbs.voxel.tilesets.geometry;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;

public class BlockGeometry
{
    public void complete()
    {}

    public int build(int nx, int ny, int nz, int index, IBlockVariant block, ChunkBuilder builder, VAOBuilder vao)
    {
        return index;
    }

    public boolean isOverlapping(BlockGeometry geometry, float x, float y, float z)
    {
        return false;
    }
}