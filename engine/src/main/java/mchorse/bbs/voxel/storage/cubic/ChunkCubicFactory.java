package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.ChunkView;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;

public class ChunkCubicFactory extends ChunkFactory
{
    public ChunkCubicFactory(File folder, BlockSet blocks, WorldMetadata metadata)
    {
        super(folder, blocks, metadata);
    }

    @Override
    public ChunkStorage createStorage(String folder)
    {
        return new ChunkCubicStorage(new File(this.folder, folder), this.metadata);
    }

    @Override
    public ChunkManager createManager()
    {
        if (this.conversion)
        {
            return new ChunkCubicConversionManager(this.blocks);
        }

        int w = this.metadata.chunks;
        ChunkCubicManager manager = new ChunkCubicManager(this.blocks, -w / 2, -3, -w / 2, w, 6, w);

        manager.setChunkSize(this.metadata.chunkSize);

        return manager;
    }

    @Override
    public ChunkView createView(ChunkArrayManager manager, World world)
    {
        return new ChunkCubicView(manager, new ChunkCubicThread(world), this.metadata);
    }
}