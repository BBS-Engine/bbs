package mchorse.bbs.game.scripts.code;

import mchorse.bbs.game.scripts.user.IScriptBlockVariant;
import mchorse.bbs.resources.Link;
import mchorse.bbs.voxel.blocks.IBlockVariant;

public class ScriptBlockVariant implements IScriptBlockVariant
{
    public IBlockVariant variant;

    public ScriptBlockVariant(IBlockVariant variant)
    {
        this.variant = variant;
    }

    @Override
    public IBlockVariant getBlockVariant()
    {
        return this.variant;
    }

    @Override
    public String getBlockId()
    {
        return this.variant.getLink().source + Link.SOURCE_SEPARATOR + this.variant.getLink().path;
    }

    @Override
    public int getVariant()
    {
        return this.variant.getLink().variant;
    }

    @Override
    public boolean isAir()
    {
        return this.variant.isAir();
    }

    @Override
    public boolean isSame(IScriptBlockVariant blockVariant)
    {
        return this.variant == blockVariant.getBlockVariant();
    }

    @Override
    public String toString()
    {
        return this.variant.getLink().toString();
    }
}