package mchorse.bbs.voxel.storage;

import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;

public abstract class ChunkFactory
{
    public File folder;
    public BlockSet blocks;
    protected final WorldMetadata metadata;
    protected boolean conversion;

    public ChunkFactory(File folder, BlockSet blocks, WorldMetadata metadata)
    {
        this.folder = folder;
        this.blocks = blocks;
        this.metadata = metadata;
    }

    public ChunkFactory conversion()
    {
        this.conversion = true;

        return this;
    }

    public WorldMetadata getMetadata()
    {
        return this.metadata;
    }

    public ChunkStorage createStorage()
    {
        return this.createStorage("chunks");
    }

    public abstract ChunkStorage createStorage(String folder);

    public abstract ChunkManager createManager();

    public abstract ChunkView createView(ChunkArrayManager manager, World world);
}