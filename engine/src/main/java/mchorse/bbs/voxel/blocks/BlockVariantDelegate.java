package mchorse.bbs.voxel.blocks;

import mchorse.bbs.voxel.tilesets.models.BlockModel;

/**
 * Block variant delegate that allows storing a block variant which
 * can be later replaced dynamically without having to replace
 * those globally.
 */
public class BlockVariantDelegate implements IBlockVariant
{
    public BlockVariant variant;

    public BlockVariantDelegate(BlockVariant variant)
    {
        this.variant = variant;
    }

    @Override
    public BlockLink getLink()
    {
        return this.variant.getLink();
    }

    @Override
    public int getGlobalId()
    {
        return this.variant.getGlobalId();
    }

    @Override
    public BlockModel getModel()
    {
        return this.variant.getModel();
    }

    @Override
    public boolean isAir()
    {
        return this.variant.isAir();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof IBlockVariant)
        {
            IBlockVariant variant = (IBlockVariant) obj;

            return this.getGlobalId() == variant.getGlobalId();
        }

        return false;
    }
}