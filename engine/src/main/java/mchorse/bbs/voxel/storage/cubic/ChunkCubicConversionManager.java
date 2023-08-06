package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class ChunkCubicConversionManager extends ChunkManager
{
    public final Map<Vector3i, ChunkCubicCell> chunks = new HashMap<>(1024);

    public ChunkCubicConversionManager(BlockSet blocks)
    {
        super(blocks);
    }

    @Override
    public ChunkCell[] getCells()
    {
        return this.chunks.values().toArray(new ChunkCubicCell[0]);
    }

    @Override
    public ChunkCell getCell(int x, int y, int z, boolean create)
    {
        int ix = MathUtils.toChunk(x, this.s);
        int iy = MathUtils.toChunk(y, this.s);
        int iz = MathUtils.toChunk(z, this.s);
        Vector3i key = Vectors.TEMP_3I.set(ix, iy, iz);
        ChunkCubicCell cell = this.chunks.get(key);

        if (cell == null && create)
        {
            this.chunks.put(new Vector3i(ix, iy, iz), cell = (ChunkCubicCell) this.createCell(ix, iy, iz));
        }

        return cell;
    }

    @Override
    public ChunkCell createCell(int x, int y, int z)
    {
        return new ChunkCubicCell(this, x, y, z);
    }
}