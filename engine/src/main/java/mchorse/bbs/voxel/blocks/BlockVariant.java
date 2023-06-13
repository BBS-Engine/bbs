package mchorse.bbs.voxel.blocks;

import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockVariant implements IBlockVariant
{
    private BlockLink link;
    private int globalId;
    private BlockModel model;

    public BlockVariant(BlockLink link, int globalId)
    {
        this.link = link;
        this.globalId = globalId;
    }

    @Override
    public BlockLink getLink()
    {
        return this.link;
    }

    @Override
    public int getGlobalId()
    {
        return this.globalId;
    }

    @Override
    public BlockModel getModel()
    {
        return this.model;
    }

    public void setModel(BlockModel model)
    {
        this.model = model;
    }

    @Override
    public boolean isAir()
    {
        return this.globalId == 0;
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