package mchorse.bbs.voxel.conversion;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.utils.AABBi;
import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;
import mchorse.bbs.world.entities.Entity;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class ConversionThread implements Runnable
{
    private IBridge bridge;
    private WorldMetadata oldMetadata;
    private WorldMetadata newMetadata;

    private Consumer<ConversionProgress> callback;
    private ConversionProgress progress = new ConversionProgress();

    public ConversionThread(IBridge bridge, WorldMetadata oldMetadata, WorldMetadata newMetadata, Consumer<ConversionProgress> callback)
    {
        this.bridge = bridge;
        this.oldMetadata = oldMetadata;
        this.newMetadata = newMetadata;
        this.callback = callback;

        new Thread(this, "Chunk conversion thread").start();
    }

    @Override
    public void run()
    {
        try
        {
            ChunkFactory oldFactory = this.oldMetadata.createFactory().conversion();
            ChunkStorage oldStorage = oldFactory.createStorage();
            World oldWorld = new World(this.bridge, oldFactory, null);

            ChunkFactory newFactory = this.newMetadata.createFactory().conversion();
            ChunkStorage newStorage = newFactory.createStorage("chunk7");
            World newWorld = new World(this.bridge, newFactory, null);

            this.postProgress("Initializing...");
            this.sleep(250);

            this.loadChunks(oldStorage, oldWorld);

            this.postProgress("Commencing block conversion operation...");
            this.sleep(250);

            this.copyBlocks(oldWorld, newWorld);

            this.postProgress("Commencing chunk saving...");
            this.sleep(250);

            this.saveNewChunks(newStorage, newWorld);

            File oldFolder = oldStorage.getFolder();
            File newFolder = newStorage.getFolder();
            File temp = new File(oldFolder.getParentFile(), "_chunks");

            while (temp.exists())
            {
                temp = new File(temp.getParentFile(), "_" + temp.getName());
            }

            oldFolder.renameTo(temp);
            newFolder.renameTo(oldFolder);

            this.postProgress("Done!");
            this.sleep(3000);
            this.postProgress("", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            this.postProgress("Chunk conversion failed with error message: " + e.getMessage());
            this.sleep(3000);
            this.postProgress("", true);
        }
    }

    private void loadChunks(ChunkStorage oldStorage, World oldWorld)
    {
        int i = 0;
        List<ChunkCell> chunkCells = oldStorage.getCells(oldWorld.chunks);

        for (ChunkCell cell : chunkCells)
        {
            oldStorage.read(oldWorld, cell);

            ChunkCell oldCell = oldWorld.chunks.getCell(cell.bounds.x, cell.bounds.y, cell.bounds.z, true);

            oldCell.copy(cell);

            i += 1;

            if (i % 10 == 0)
            {
                this.postProgress("Loaded " + i + "/" + chunkCells.size() + " chunk files...");
            }
        }
    }

    private void copyBlocks(World oldWorld, World newWorld)
    {
        int i = 0;
        ChunkCell[] cells = oldWorld.chunks.getCells();

        for (ChunkCell cell : cells)
        {
            AABBi bounds = cell.bounds;

            for (int x = 0; x < bounds.w; x++)
            {
                for (int y = 0; y < bounds.h; y++)
                {
                    for (int z = 0; z < bounds.d; z++)
                    {
                        int wx = bounds.x + x;
                        int wy = bounds.y + y;
                        int wz = bounds.z + z;

                        newWorld.chunks.setBlock(wx, wy, wz, oldWorld.chunks.getBlock(wx, wy, wz), false, false);
                    }
                }
            }

            i += 1;

            if (i % 10 == 0)
            {
                this.postProgress("Copied " + i + "/" + cells.length + " chunks...");
            }
        }

        newWorld.entities = oldWorld.entities;

        for (Entity entity : newWorld.entities)
        {
            entity.setWorld(newWorld);
        }
    }

    private void saveNewChunks(ChunkStorage newStorage, World newWorld)
    {
        ChunkCell[] newCells = newWorld.chunks.getCells();
        int i = 0;

        for (ChunkCell cell : newCells)
        {
            newStorage.save(newWorld, cell);

            i += 1;

            if (i % 10 == 0)
            {
                this.postProgress("Saved " + i + "/" + newCells.length + " chunks...");
            }
        }
    }

    private void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception e)
        {}
    }

    private void postProgress(String message)
    {
        this.postProgress(message, false);
    }

    private void postProgress(String message, boolean finished)
    {
        this.progress.message = message;
        this.progress.finished = finished;

        this.callback.accept(this.progress);
    }
}