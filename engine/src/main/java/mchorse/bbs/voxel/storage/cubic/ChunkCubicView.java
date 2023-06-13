package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.voxel.storage.ChunkThread;
import mchorse.bbs.voxel.storage.ChunkView;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.world.WorldMetadata;

public class ChunkCubicView extends ChunkView
{
    public ChunkCubicView(ChunkArrayManager manager, ChunkThread thread, WorldMetadata metadata)
    {
        super(manager, thread, metadata);
    }
}