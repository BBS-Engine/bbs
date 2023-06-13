package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.voxel.storage.ChunkThread;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.World;

public class ChunkCubicThread extends ChunkThread
{
    public ChunkCubicThread(World world)
    {
        super(world);
    }

    @Override
    protected void generate(ChunkCell cell, World world)
    {
        world.generator.generate(cell.getDisplay(0, 0, 0), world.chunks);
    }
}