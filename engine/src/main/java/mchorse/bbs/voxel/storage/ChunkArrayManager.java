package mchorse.bbs.voxel.storage;

import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Vector2f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

/**
 * Chunk manager (flat array implementation).
 *
 * This implementation is used for limited viewport design for fast random access
 * with limitation of not being able to have chunks arbitrarily located in the
 * chunk manager (unlike conversion managers which use hash maps).
 */
public abstract class ChunkArrayManager extends ChunkManager
{
    public final List<ChunkCell> render = new ArrayList<ChunkCell>();
    protected ChunkCell[] chunks;

    /* Chunk view position (in terms of chunks, like 16 blocks per unit) */
    protected int x;
    protected int y;
    protected int z;

    /* Chunk view dimension (in terms of chunks, like 16 blocks per unit) */
    protected int w;
    protected int h;
    protected int d;

    public ChunkArrayManager(BlockSet models, int x, int y, int z, int w, int h, int d)
    {
        super(models);

        this.x = x;
        this.y = y;
        this.z = z;

        this.w = w;
        this.h = h;
        this.d = d;
    }

    @Override
    public int getW()
    {
        return this.w;
    }

    @Override
    public int getH()
    {
        return this.h;
    }

    @Override
    public int getD()
    {
        return this.d;
    }

    public void setXYZ(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public abstract int getGlobalIndex(int x, int y, int z);

    public abstract Vector3i getVectorFromIndex(int i, Vector3i vector);

    public void sortFromOrigin()
    {
        float dx = (this.x + this.w / 2) * this.s;
        float dz = (this.z + this.d / 2) * this.s;

        this.dirty.sort((a, b) ->
        {
            float distanceA = new Vector2f(a.x, a.z).distance(dx, dz);
            float distanceB = new Vector2f(b.x, b.z).distance(dx, dz);

            return Float.compare(distanceA, distanceB);
        });
    }

    public boolean isOutside(int x, int y, int z)
    {
        return x < this.x * this.s || x >= (this.x + this.w) * this.s
            || y < this.y * this.s || y >= (this.y + this.h) * this.s
            || z < this.z * this.s || z >= (this.z + this.d) * this.s;
    }

    @Override
    public void rebuild()
    {
        for (ChunkCell cell : this.render)
        {
            cell.dirty();
        }

        this.sortFromOrigin();
    }
}