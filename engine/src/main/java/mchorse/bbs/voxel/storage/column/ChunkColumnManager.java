package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Vector3i;

public class ChunkColumnManager extends ChunkArrayManager
{
    public ChunkColumnManager(BlockSet models, int x, int y, int z, int w, int h, int d)
    {
        super(models, x, y, z, w, h, d);

        this.chunks = new ChunkColumnCell[this.w * this.d];
    }

    @Override
    public void setXYZ(int x, int y, int z)
    {
        y = this.y;

        super.setXYZ(x, y, z);
    }

    @Override
    public ChunkCell[] getCells()
    {
        return this.chunks;
    }

    @Override
    public ChunkCell getCell(int x, int y, int z, boolean create)
    {
        int i = this.getGlobalIndex(x, y, z);

        if (i < 0 || i >= this.w * this.d)
        {
            return null;
        }

        ChunkCell cell = this.chunks[i];

        if (cell == null && create)
        {
            int ix = MathUtils.toChunk(x, this.s);
            int iz = MathUtils.toChunk(z, this.s);

            cell = this.createCell(ix, 0, iz);

            this.chunks[i] = cell;
            this.render.add(cell);
            cell.dirty();
        }

        return cell;
    }

    @Override
    public ChunkCell createCell(int x, int y, int z)
    {
        return new ChunkColumnCell(this, x, this.y, z, this.h);
    }

    @Override
    public int getGlobalIndex(int x, int y, int z)
    {
        x -= this.x * this.s;
        y -= this.y * this.s;
        z -= this.z * this.s;

        if (x < 0 || x >= this.w * this.s || y < 0 || y >= this.h * this.s || z < 0 || z >= this.d * this.s)
        {
            return -1;
        }

        return x / this.s + z / this.s * this.w;
    }

    @Override
    public Vector3i getVectorFromIndex(int i, Vector3i vector)
    {
        if (i < 0 || i >= this.chunks.length)
        {
            return null;
        }

        return vector.set(i % this.w, 0, i / this.w);
    }
}