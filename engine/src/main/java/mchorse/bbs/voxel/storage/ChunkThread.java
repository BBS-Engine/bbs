package mchorse.bbs.voxel.storage;

import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.World;
import org.joml.Vector3i;

import java.util.Stack;

/**
 * Abstract chunk thread
 *
 * Subclasses are responsible for running a dedicated worker responsible for asynchronously
 * loading chunks in the world.
 */
public abstract class ChunkThread implements Runnable
{
    protected Stack<Vector3i> load = new Stack<Vector3i>();
    protected Stack<ChunkCell> save = new Stack<ChunkCell>();

    protected World world;
    private boolean stop;
    private boolean saveAll;

    private Thread thread;

    public ChunkThread(World world)
    {
        this.world = world;
    }

    /**
     * Start chunk thread
     */
    public void start()
    {
        if (this.thread == null)
        {
            this.thread = new Thread(this, "Chunk Generator");

            this.thread.start();
        }
    }

    public void stop(boolean saveAll)
    {
        this.stop = true;
        this.saveAll = saveAll;

        for (ChunkCell cell : this.world.chunks.getCells())
        {
            if (cell.unsaved)
            {
                this.addToSave(cell);
            }
        }
    }

    /**
     * Add a chunk coordinates that much be read from file or generated
     * asynchronously by thread.
     */
    public void addToLoad(int cx, int cy, int cz)
    {
        this.load.add(new Vector3i(cx, cy, cz));
    }

    /**
     * Add a chunk cell that much be saved (because being unloaded).
     */
    public void addToSave(ChunkCell cell)
    {
        if (cell != null)
        {
            this.save.add(cell);
        }
    }

    public boolean isIdling()
    {
        return this.load.isEmpty();
    }

    public boolean isSaving()
    {
        return !this.save.isEmpty();
    }

    @Override
    public void run()
    {
        while (!this.stop)
        {
            if (!this.save.isEmpty())
            {
                this.saveCell(this.save.pop());
            }

            if (this.isIdling())
            {
                try
                {
                    Thread.sleep(20);

                    continue;
                }
                catch (Exception e)
                {}
            }

            this.load(this.load.pop());
        }

        this.saveRemaining(this.saveAll);

        this.thread = null;
    }

    /**
     * Save next chunk cell in the stack.
     */
    protected void saveCell(ChunkCell cell)
    {
        cell.removed = true;

        this.world.save(cell);
    }

    /**
     * Save remaining chunks that weren't saved yet (or all chunks in the world).
     */
    protected void saveRemaining(boolean all)
    {
        if (all)
        {
            this.world.saveAll(false);
        }
        else
        {
            while (!this.save.isEmpty())
            {
                this.world.save(this.save.pop());
            }
        }
    }

    /**
     * Load a chunk cell at given XYZ chunk coordinates.
     */
    protected void load(Vector3i entry)
    {
        int s = this.world.chunks.s;
        ChunkCell cell = this.world.chunks.getCell(entry.x * s, entry.y * s, entry.z * s, true);

        if (cell == null || cell.generated)
        {
            return;
        }

        if (!this.world.read(cell))
        {
            this.generate(cell, this.world);
        }

        cell.generated = true;
    }

    /**
     * Generate chunk cell in case it wasn't read from file.
     */
    protected abstract void generate(ChunkCell cell, World world);
}