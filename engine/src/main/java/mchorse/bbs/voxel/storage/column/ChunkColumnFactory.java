package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.ChunkView;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;

public class ChunkColumnFactory extends ChunkFactory
{
    public ChunkColumnFactory(File folder, BlockSet blocks, WorldMetadata metadata)
    {
        super(folder, blocks, metadata);
    }

    @Override
    public ChunkStorage createStorage(String folder)
    {
        return new ChunkColumnStorage(new File(this.folder, folder), this.metadata);
    }

    @Override
    public ChunkManager createManager()
    {
        if (this.conversion)
        {
            return new ChunkColumnConversionManager(this.blocks, this.metadata.columnBase, this.metadata.columnHeight);
        }

        int w = this.metadata.chunks;
        ChunkColumnManager manager = new ChunkColumnManager(this.blocks, -w / 2, this.metadata.columnBase, -w / 2, w, this.metadata.columnHeight, w);

        manager.setChunkSize(this.metadata.chunkSize);

        return manager;
    }

    @Override
    public ChunkView createView(ChunkArrayManager manager, World world)
    {
        return new ChunkColumnView(manager, new ChunkColumnThread(world), this.metadata);
    }
}