package mchorse.bbs.voxel.storage;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.IBlockAccessor;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Vector3i;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class ChunkManager implements IDisposable, IBlockAccessor
{
    public final ChunkBuilder builder;
    public final List<ChunkDisplay> dirty = new ArrayList<>();

    /**
     * Chunk display size (how many blocks in a chunk display by width, height and depth).
     */
    public int s = 16;

    public int getW()
    {
        return 1;
    }

    public int getH()
    {
        return 1;
    }

    public int getD()
    {
        return 1;
    }

    public ChunkManager(BlockSet models)
    {
        this.builder = new ChunkBuilder(models);
    }

    public void setChunkSize(int size)
    {
        this.s = MathUtils.clamp(size, 2, 128);
    }

    /**
     * Get all available cells in this chunk manager.
     */
    public abstract ChunkCell[] getCells();

    public abstract ChunkCell getCell(int x, int y, int z, boolean create);

    public abstract ChunkCell createCell(int x, int y, int z);

    /**
     * Get chunk display at global block coordinates.
     */
    public ChunkDisplay getDisplay(int x, int y, int z)
    {
        ChunkCell cell = this.getCell(x, y, z, false);

        return cell == null ? null : cell.getDisplay(x, y, z);
    }

    /**
     * Get block at global block coordinates.
     */
    @Override
    public IBlockVariant getBlock(int x, int y, int z)
    {
        ChunkCell cell = this.getCell(x, y, z, false);

        return cell == null ? this.builder.models.air : cell.getBlock(x, y, z);
    }

    /**
     * Set block at global block coordinates and notify neighbors.
     */
    public void setBlock(int x, int y, int z, IBlockVariant block)
    {
        this.setBlock(x, y, z, block, true, false);
    }

    /**
     * Set block at global block coordinates and notify neighbors.
     */
    public void setBlockForced(int x, int y, int z, IBlockVariant block)
    {
        this.setBlock(x, y, z, block, true, true);
    }

    /**
     * Set block at global block coordinates.
     */
    public void setBlock(int x, int y, int z, IBlockVariant block, boolean notify, boolean priority)
    {
        ChunkCell cell = this.getCell(x, y, z, true);

        if (cell != null)
        {
            if (priority)
            {
                cell.generated = true;
            }

            IBlockVariant old = cell.getBlock(x, y, z);

            if (!cell.setBlock(x, y, z, block, priority))
            {
                return;
            }

            if (old != block)
            {
                this.propagateLight(new Vector3i(x, y, z), block.getModel().lighting);
            }

            if (!notify)
            {
                return;
            }

            this.markNeighbors(x, y, z, priority);
        }
    }

    /**
     * Mark neighbor chunks for update.
     */
    protected void markNeighbors(int x, int y, int z, boolean priority)
    {
        int size = this.s;
        int modX = x - MathUtils.toChunk(x, size) * size;
        int modY = y - MathUtils.toChunk(y, size) * size;
        int modZ = z - MathUtils.toChunk(z, size) * size;
        int edge = size - 1;

        /* There is no point in doing extra checks, if the block is not anywhere adjacent
         * on corner, or on edge of the chunk. */
        if (modX > 0 && modX < edge && modY > 0 && modY < edge && modZ > 0 && modZ < edge)
        {
            return;
        }

        /* Notify adjacent chunks */
        if (modX == 0) this.markDirty(x - 1, y, z, priority);
        else if (modX == edge) this.markDirty(x + 1, y, z, priority);

        if (modY == 0) this.markDirty(x, y - 1, z, priority);
        else if (modY == edge) this.markDirty(x, y + 1, z, priority);

        if (modZ == 0) this.markDirty(x, y, z - 1, priority);
        else if (modZ == edge) this.markDirty(x, y, z + 1, priority);

        /* Notify edge chunks */
        if (modY == 0 && modX == 0) this.markDirty(x - 1, y - 1, z, priority);
        if (modY == 0 && modX == edge) this.markDirty(x + 1, y - 1, z, priority);
        if (modY == 0 && modZ == 0) this.markDirty(x, y - 1, z - 1, priority);
        if (modY == 0 && modZ == edge) this.markDirty(x, y - 1, z + 1, priority);

        if (modY == edge && modX == 0) this.markDirty(x - 1, y + 1, z, priority);
        if (modY == edge && modX == edge) this.markDirty(x + 1, y + 1, z, priority);
        if (modY == edge && modZ == 0) this.markDirty(x, y + 1, z - 1, priority);
        if (modY == edge && modZ == edge) this.markDirty(x, y + 1, z + 1, priority);

        if (modX == 0 && modZ == 0) this.markDirty(x - 1, y, z - 1, priority);
        if (modX == 0 && modZ == edge) this.markDirty(x - 1, y, z + 1, priority);
        if (modX == edge && modZ == 0) this.markDirty(x + 1, y, z - 1, priority);
        if (modX == edge && modZ == edge) this.markDirty(x + 1, y, z + 1, priority);

        /* Notify corner chunks */
        if (modX == 0 && modY == 0 && modZ == 0) this.markDirty(x - 1, y - 1, z - 1, priority);
        if (modX == edge && modY == 0 && modZ == 0) this.markDirty(x + 1, y - 1, z - 1, priority);
        if (modX == 0 && modY == 0 && modZ == edge) this.markDirty(x - 1, y - 1, z + 1, priority);
        if (modX == edge && modY == 0 && modZ == edge) this.markDirty(x + 1, y - 1, z + 1, priority);

        if (modX == 0 && modY == edge && modZ == 0) this.markDirty(x - 1, y + 1, z - 1, priority);
        if (modX == edge && modY == edge && modZ == 0) this.markDirty(x + 1, y + 1, z - 1, priority);
        if (modX == 0 && modY == edge && modZ == edge) this.markDirty(x - 1, y + 1, z + 1, priority);
        if (modX == edge && modY == edge && modZ == edge) this.markDirty(x + 1, y + 1, z + 1, priority);
    }

    /**
     * Propagate light
     *
     * @link https://web.archive.org/web/20201003052751/https://www.seedofandromeda.com/blogs/29-fast-flood-fill-lighting-in-a-blocky-voxel-game-pt-1
     */
    protected void propagateLight(Vector3i block, int light)
    {
        Queue<Vector3i> addingLight = new LinkedList<>();

        if (light == 0)
        {
            Queue<Vector4i> removingLight = new LinkedList<>();
            int lighting = this.getLighting(block.x, block.y, block.z);

            removingLight.add(new Vector4i(block.x, block.y, block.z, lighting));

            this.setLighting(block.x, block.y, block.z, 0);

            while (!removingLight.isEmpty())
            {
                Vector4i p = removingLight.poll();

                this.removeLightFurther(new Vector4i(p).add(0, 1, 0, 0), removingLight, addingLight);
                this.removeLightFurther(new Vector4i(p).add(0, -1, 0, 0), removingLight, addingLight);
                this.removeLightFurther(new Vector4i(p).add(1, 0, 0, 0), removingLight, addingLight);
                this.removeLightFurther(new Vector4i(p).add(-1, 0, 0, 0), removingLight, addingLight);
                this.removeLightFurther(new Vector4i(p).add(0, 0, 1, 0), removingLight, addingLight);
                this.removeLightFurther(new Vector4i(p).add(0, 0, -1, 0), removingLight, addingLight);
            }
        }
        else
        {
            this.setLighting(block.x, block.y, block.z, light);

            addingLight.add(block);
        }

        while (!addingLight.isEmpty())
        {
            Vector3i p = addingLight.poll();
            int pLight = this.getLighting(p.x, p.y, p.z);

            this.propagateLightFurther(new Vector3i(p).add(0, 1, 0), addingLight, pLight);
            this.propagateLightFurther(new Vector3i(p).add(0, -1, 0), addingLight, pLight);
            this.propagateLightFurther(new Vector3i(p).add(1, 0, 0), addingLight, pLight);
            this.propagateLightFurther(new Vector3i(p).add(-1, 0, 0), addingLight, pLight);
            this.propagateLightFurther(new Vector3i(p).add(0, 0, 1), addingLight, pLight);
            this.propagateLightFurther(new Vector3i(p).add(0, 0, -1), addingLight, pLight);
        }
    }

    /**
     * If conditions are met to propagate light further, it will propagate further
     */
    protected void propagateLightFurther(Vector3i block, Queue<Vector3i> queue, int light)
    {
        int blockLight = this.getLighting(block.x, block.y, block.z);
        IBlockVariant variant = this.getBlock(block.x, block.y, block.z);

        if (blockLight < light - 2 && !variant.getModel().opaque)
        {
            queue.add(block);

            ChunkCell cell = this.getCell(block.x, block.y, block.z, false);

            if (cell != null)
            {
                cell.dirty();
                cell.saveLater();
                cell.setLighting(block.x, block.y, block.z, light - 1);
                this.markNeighbors(block.x, block.y, block.z, true);
            }
        }
    }

    /**
     * If conditions are met to remove light further, it will remove further,
     * otherwise, the light will have to be propagated
     */
    protected void removeLightFurther(Vector4i block, Queue<Vector4i> removing, Queue<Vector3i> adding)
    {
        int light = block.w;
        int blockLight = this.getLighting(block.x, block.y, block.z);
        IBlockVariant variant = this.getBlock(block.x, block.y, block.z);

        if (blockLight != 0 && blockLight < light && !variant.getModel().opaque)
        {
            removing.add(block);

            ChunkCell cell = this.getCell(block.x, block.y, block.z, false);

            cell.dirty();
            cell.saveLater();
            cell.setLighting(block.x, block.y, block.z, 0);
            this.markNeighbors(block.x, block.y, block.z, true);
        }
        else if (blockLight >= light)
        {
            adding.add(new Vector3i(block.x, block.y, block.z));
        }
    }

    public int getLighting(int x, int y, int z)
    {
        ChunkDisplay display = this.getDisplay(x, y, z);

        if (display != null)
        {
            return display.chunk.getLighting(x - display.x, y - display.y, z - display.z);
        }

        return 0;
    }

    public void setLighting(int x, int y, int z, int light)
    {
        ChunkCell cell = this.getCell(x, y, z, false);

        if (cell != null)
        {
            cell.setLighting(x, y, z, light);
        }
    }

    /**
     * Mark chunk display dirty at given global block coordinates XYZ.
     */
    public void markDirty(int x, int y, int z, boolean priority)
    {
        ChunkDisplay display = this.getDisplay(x, y, z);

        if (display != null)
        {
            display.dirty(priority);
        }
    }

    public boolean isOutside(int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean hasBlock(int x, int y, int z)
    {
        if (this.isOutside(x, y, z))
        {
            return false;
        }

        return !this.getBlock(x, y, z).isAir();
    }

    public void rebuild()
    {}

    public void buildChunks(RenderingContext context, boolean forceAll)
    {
        if (this.dirty.isEmpty())
        {
            return;
        }

        Iterator<ChunkDisplay> it = this.dirty.iterator();
        long time = System.currentTimeMillis();

        while (it.hasNext() && (System.currentTimeMillis() - time < 4 || forceAll))
        {
            ChunkDisplay display = it.next();

            if (!display.parent.generated)
            {
                display.dirty = false;
                it.remove();

                continue;
            }

            this.builder.build(context, display, this);
            it.remove();

            display.dirty = false;
        }
    }

    /**
     * Free up any OpenGL or native memory that was used by this chunk manager.
     */
    @Override
    public void delete()
    {
        for (ChunkCell cell : this.getCells())
        {
            if (cell != null)
            {
                cell.delete();
            }
        }
    }
}