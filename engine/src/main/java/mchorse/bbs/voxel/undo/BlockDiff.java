package mchorse.bbs.voxel.undo;

import mchorse.bbs.voxel.blocks.IBlockVariant;
import org.joml.Vector3i;

public class BlockDiff
{
    public Vector3i position;
    public IBlockVariant previous;
    public IBlockVariant current;

    public BlockDiff(Vector3i position, IBlockVariant previous, IBlockVariant current)
    {
        this.position = position;
        this.previous = previous;
        this.current = current;
    }
}