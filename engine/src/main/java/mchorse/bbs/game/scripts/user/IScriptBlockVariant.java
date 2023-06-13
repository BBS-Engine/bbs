package mchorse.bbs.game.scripts.user;

import mchorse.bbs.voxel.blocks.IBlockVariant;

/**
 * Script representation of a block variant.
 */
public interface IScriptBlockVariant
{
    /**
     * Get raw instance of this block variant.
     */
    public IBlockVariant getBlockVariant();

    /**
     * Get block ID of this block variant.
     */
    public String getBlockId();

    /**
     * Get integer (index) of the block variant.
     */
    public int getVariant();

    /**
     * Check if this block variant is an air block.
     */
    public boolean isAir();

    /**
     * Check if the given block variant is same as this.
     */
    public boolean isSame(IScriptBlockVariant blockVariant);
}