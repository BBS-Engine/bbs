package mchorse.bbs.voxel.storage.data;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.voxel.Chunk;

public class ChunkDisplay implements IDisposable
{
    public ChunkCell parent;

    public Chunk chunk;
    public VAO display;

    /* Chunk display's position (in terms of chunks) */
    public final int x;
    public final int y;
    public final int z;

    public boolean wasDirty;
    public boolean dirty;

    public ChunkDisplay(ChunkCell parent, Chunk chunk, int x, int y, int z)
    {
        this.parent = parent;
        this.chunk = chunk;

        this.x = x * chunk.w;
        this.y = y * chunk.h;
        this.z = z * chunk.d;
    }

    /**
     * Cache current dirty value.
     */
    public void cacheDirty()
    {
        this.wasDirty = this.dirty;
    }

    /**
     * Mark this chunk display as dirty.
     */
    public void dirty()
    {
        this.dirty(false);
    }

    /**
     * Mark this chunk display as dirty with optional priority flag (that
     * inserts chunk display in the front of dirty chunk display list).
     */
    public void dirty(boolean priority)
    {
        if (!this.dirty)
        {
            if (priority)
            {
                this.parent.manager.dirty.add(0, this);
            }
            else
            {
                this.parent.manager.dirty.add(this);
            }
        }

        this.dirty = true;
    }

    public void render()
    {
        if (this.display != null)
        {
            this.display.bindForRender();
            this.display.renderElements();
            this.display.unbindForRender();
        }
    }

    @Override
    public void delete()
    {
        if (this.display != null)
        {
            this.display.delete();

            this.display = null;
        }
    }
}