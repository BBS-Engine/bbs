package mchorse.bbs.voxel.blocks;

import mchorse.bbs.voxel.tilesets.models.BlockModel;

public interface IBlockVariant
{
    /**
     * Get block variant identifier
     */
    public BlockLink getLink();

    /**
     * Get block variant's global integer ID
     */
    public int getGlobalId();

    /**
     * Get block variant's block model
     */
    public BlockModel getModel();

    /**
     * Check whether this block variant represents air
     */
    public boolean isAir();
}