package mchorse.bbs.voxel.storage;

import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.WorldMetadata;
import org.joml.Vector3d;
import org.joml.Vector3i;

/**
 * Chunk view
 *
 * This class is responsible for loading and saving view at given camera center,
 * limiting world view to whatever chunk manager is doing.
 */
public abstract class ChunkView
{
    protected ChunkArrayManager manager;
    protected ChunkThread thread;
    protected WorldMetadata metadata;
    private ChunkCell[] cache;

    private Vector3i vector = new Vector3i();
    private int lastX = Integer.MIN_VALUE;
    private int lastY = Integer.MIN_VALUE;
    private int lastZ = Integer.MIN_VALUE;

    public ChunkView(ChunkArrayManager manager, ChunkThread thread, WorldMetadata metadata)
    {
        this.manager = manager;
        this.thread = thread;
        this.metadata = metadata;

        this.cache = new ChunkCell[manager.chunks.length];
    }

    public ChunkThread getThread()
    {
        return this.thread;
    }

    public void updateChunks(Vector3d vector)
    {
        int cx = MathUtils.toChunk(this.metadata.limitX.apply(vector.x), this.manager.s);
        int cy = MathUtils.toChunk(this.metadata.limitY.apply(vector.y), this.manager.s);
        int cz = MathUtils.toChunk(this.metadata.limitZ.apply(vector.z), this.manager.s);

        if (cx == this.lastX && cy == this.lastY && cz == this.lastZ)
        {
            return;
        }

        ChunkArrayManager chunks = this.manager;

        chunks.setXYZ(cx - chunks.w / 2, cy - chunks.h / 2, cz - chunks.d / 2);
        chunks.render.clear();
        chunks.dirty.clear();

        /* Transfer relevant chunks and unload out of render distance ones */
        for (int i = 0, c = this.cache.length; i < c; i++)
        {
            this.cache[i] = chunks.chunks[i];
            chunks.chunks[i] = null;
        }

        for (int i = 0, c = this.cache.length; i < c; i++)
        {
            ChunkCell cell = this.cache[i];

            if (cell != null)
            {
                int index = chunks.getGlobalIndex(cell.bounds.x, cell.bounds.y, cell.bounds.z);

                if (index >= 0 && index < chunks.chunks.length)
                {
                    cell.pushDirty();

                    chunks.chunks[index] = cell;
                    chunks.render.add(cell);

                    cell.popDirty();
                }
                else
                {
                    cell.delete();

                    if (cell.unsaved && cell.generated)
                    {
                        this.thread.addToSave(cell);
                    }
                }
            }
        }

        /* Load new ones */
        int dcx = cx - this.lastX;
        int dcy = cy - this.lastY;
        int dcz = cz - this.lastZ;

        for (int i = 0, count = chunks.chunks.length; i < count; i++)
        {
            ChunkCell cell = chunks.chunks[i];

            if (cell == null || !cell.generated)
            {
                Vector3i vec = chunks.getVectorFromIndex(i, this.vector);

                int xx = chunks.x + vec.x;
                int yy = chunks.y + vec.y;
                int zz = chunks.z + vec.z;

                chunks.getCell(xx * chunks.s, yy * chunks.s, zz * chunks.s, true);
                this.thread.addToLoad(xx, yy, zz);

                /* Rebuild optimized chunk
                 *
                 * Basically, there is a bug that once you travel some distance, you could see missing
                 * faces on some block sides on the edges of the previous chunks */
                ChunkCell optimized = chunks.getCell((xx - dcx) * chunks.s, (yy - dcy) * chunks.s, (zz - dcz) * chunks.s, false);

                if (optimized != null)
                {
                    optimized.dirty();
                }
            }
        }

        this.lastX = cx;
        this.lastY = cy;
        this.lastZ = cz;

        chunks.sortFromOrigin();
    }
}