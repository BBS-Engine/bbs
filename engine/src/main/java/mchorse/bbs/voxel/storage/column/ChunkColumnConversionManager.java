package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class ChunkColumnConversionManager extends ChunkManager
{
    public final Map<Vector2i, ChunkColumnCell> chunks = new HashMap<Vector2i, ChunkColumnCell>(256);

    public int y;
    public int h;

    public ChunkColumnConversionManager(BlockSet blocks, int y, int h)
    {
        super(blocks);

        this.y = y;
        this.h = h;
    }

    @Override
    public ChunkCell[] getCells()
    {
        return this.chunks.values().toArray(new ChunkColumnCell[0]);
    }

    @Override
    public ChunkCell getCell(int x, int y, int z, boolean create)
    {
        int ix = MathUtils.toChunk(x, this.s);
        int iz = MathUtils.toChunk(z, this.s);
        Vector2i key = Vectors.TEMP_2I.set(ix, iz);
        ChunkColumnCell cell = this.chunks.get(key);

        if (cell == null && create)
        {
            this.chunks.put(new Vector2i(ix, iz), cell = (ChunkColumnCell) this.createCell(ix, 0, iz));
        }

        return cell;
    }

    @Override
    public ChunkCell createCell(int x, int y, int z)
    {
        return new ChunkColumnCell(this, x, this.y, z, this.h);
    }
}