package mchorse.bbs.voxel.tilesets.geometry;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Combined geometry
 * 
 * This class is responsible for combining multiple block geometry 
 * entries.
 */
public class CombinedGeometry extends BlockGeometry
{
    public List<BlockGeometry> geometries = new ArrayList<BlockGeometry>();

    public CombinedGeometry(BlockGeometry... geometries)
    {
        this.geometries.addAll(Arrays.asList(geometries));
    }

    @Override
    public void complete()
    {
        for (BlockGeometry geometry : this.geometries)
        {
            geometry.complete();
        }
    }

    @Override
    public int build(int nx, int ny, int nz, int index, IBlockVariant block, ChunkBuilder builder, VAOBuilder vao)
    {
        for (BlockGeometry geometry : this.geometries)
        {
            index = geometry.build(nx, ny, nz, index, block, builder, vao);
        }

        return index;
    }

    @Override
    public boolean isOverlapping(BlockGeometry geometry, float x, float y, float z)
    {
        if (!(geometry instanceof CombinedGeometry))
        {
            return false;
        }

        CombinedGeometry combined = (CombinedGeometry) geometry;
        int size = this.geometries.size();

        if (combined.geometries.size() != size)
        {
            return false;
        }

        for (int i = 0; i < size; i++)
        {
            if (!this.geometries.get(i).isOverlapping(combined.geometries.get(i), x, y, z))
            {
                return false;
            }
        }

        return true;
    }
}