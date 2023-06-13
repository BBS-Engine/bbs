package mchorse.bbs.voxel.undo;

import mchorse.bbs.world.World;
import mchorse.bbs.utils.undo.IUndo;

import java.util.List;

public class BlocksUndo implements IUndo<World>
{
    public List<BlockDiff> blocks;

    public BlocksUndo(List<BlockDiff> blocks)
    {
        this.blocks = blocks;
    }

    @Override
    public IUndo<World> noMerging()
    {
        return this;
    }

    @Override
    public boolean isMergeable(IUndo<World> undo)
    {
        return false;
    }

    @Override
    public void merge(IUndo<World> undo)
    {}

    @Override
    public void undo(World context)
    {
        for (BlockDiff diff : this.blocks)
        {
            context.chunks.setBlockForced(diff.position.x, diff.position.y, diff.position.z, diff.previous);
        }
    }

    @Override
    public void redo(World context)
    {
        for (BlockDiff diff : this.blocks)
        {
            context.chunks.setBlockForced(diff.position.x, diff.position.y, diff.position.z, diff.current);
        }
    }
}