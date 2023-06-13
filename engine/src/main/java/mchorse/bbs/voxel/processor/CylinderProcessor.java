package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class CylinderProcessor extends FillProcessor
{
    public CylinderProcessor(IBlockVariant block, boolean hollow)
    {
        super(block, hollow);
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        double distance = this.distance(x, z);
        int dy = y - this.min.y;
        boolean within = distance < 1;

        if (this.hollow)
        {
            if (within && ((dy == 0 || dy == this.volume.y - 1) || this.checkNeighbors(x, z)))
            {
                proxy.setBlock(x, y, z, this.block);
            }
        }
        else if (within)
        {
            proxy.setBlock(x, y, z, this.block);
        }
    }

    private boolean checkNeighbors(int x, int z)
    {
        return this.distance(x + 1, z) >= 1 || this.distance(x - 1, z) >= 1  /* X neighbors */
            || this.distance(x, z + 1) >= 1 || this.distance(x, z - 1) >= 1; /* Z neighbors */
    }

    private double distance(int x, int z)
    {
        double dx = this.radius(x, this.min.x, this.volume.x);
        double dz = this.radius(z, this.min.z, this.volume.z);

        return dx * dx + dz * dz;
    }
}