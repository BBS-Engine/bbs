package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class SphereProcessor extends FillProcessor
{
    public SphereProcessor(IBlockVariant block, boolean hollow)
    {
        super(block, hollow);
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        double distance = this.distance(x, y, z);
        boolean within = distance < 1;

        if (this.hollow)
        {
            if (within && this.checkNeighbors(x, y, z))
            {
                proxy.setBlock(x, y, z, this.block);
            }
        }
        else if (within)
        {
            proxy.setBlock(x, y, z, this.block);
        }
    }

    private boolean checkNeighbors(int x, int y, int z)
    {
        return this.distance(x + 1, y, z) >= 1 || this.distance(x - 1, y, z) >= 1  /* X neighbors */
            || this.distance(x, y + 1, z) >= 1 || this.distance(x, y - 1, z) >= 1  /* Y neighbors */
            || this.distance(x, y, z + 1) >= 1 || this.distance(x, y, z - 1) >= 1; /* Z neighbors */
    }

    private double distance(int x, int y, int z)
    {
        double dx = this.radius(x, this.min.x, this.volume.x);
        double dy = this.radius(y, this.min.y, this.volume.y);
        double dz = this.radius(z, this.min.z, this.volume.z);

        return dx * dx + dy * dy + dz * dz;
    }
}