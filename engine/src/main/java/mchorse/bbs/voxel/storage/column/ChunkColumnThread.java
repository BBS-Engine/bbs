package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.voxel.storage.ChunkThread;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.World;

public class ChunkColumnThread extends ChunkThread
{
    public ChunkColumnThread(World world)
    {
        super(world);
    }

    @Override
    protected void generate(ChunkCell cell, World world)
    {
        ChunkColumnCell column = (ChunkColumnCell) cell;

        for (int i = 0; i < column.displays.length; i++)
        {
            world.generator.generate(column.displays[i], world.chunks);
        }
    }
}