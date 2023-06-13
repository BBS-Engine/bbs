package mchorse.bbs.voxel.storage;

import mchorse.bbs.data.storage.DataFileStorage;
import mchorse.bbs.data.storage.DataGzipStorage;
import mchorse.bbs.data.storage.DataStorage;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;
import mchorse.bbs.world.entities.Entity;

import java.io.File;
import java.util.List;

/**
 * Abstract chunk storage
 *
 * Subclasses are responsible for saving and loading chunk data from a folder.
 */
public abstract class ChunkStorage
{
    protected File folder;
    protected WorldMetadata metadata;

    public ChunkStorage(File folder, WorldMetadata metadata)
    {
        if (folder != null)
        {
            this.folder = folder;
            this.folder.mkdirs();
        }

        this.metadata = metadata;
    }

    public File getFolder()
    {
        return this.folder;
    }

    public void save(World world, ChunkCell cell)
    {
        MapType map = cell.toData();

        if (cell.removed)
        {
            for (Entity entity : cell.entities)
            {
                world.removeEntitySafe(entity);
            }
        }

        try
        {
            this.createStorage(this.getFile(cell)).write(map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean read(World world, ChunkCell cell)
    {
        try
        {
            File file = this.getFile(cell);
            MapType map = (MapType) this.createStorage(file).read();

            if (map != null)
            {
                cell.fromData(world.architect, map);

                for (Entity entity : cell.entities)
                {
                    world.addEntitySafe(entity);
                }
            }

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    protected DataStorage createStorage(File file)
    {
        DataStorage storage = new DataFileStorage(file);

        return this.metadata.compress ? new DataGzipStorage(storage) : storage;
    }

    protected abstract File getFile(ChunkCell cell);

    public abstract List<ChunkCell> getCells(ChunkManager manager);
}