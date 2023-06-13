package mchorse.bbs.voxel.undo;

import mchorse.bbs.utils.undo.UndoManager;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.World;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkProxy
{
    private ChunkManager chunks;
    private UndoManager<World> undoManager;
    private boolean recording;
    private Map<Vector3i, BlockDiff> blocks = new HashMap<Vector3i, BlockDiff>();

    private List<IBlockVariant> mask = new ArrayList<IBlockVariant>();
    private boolean maskEnabled = true;

    public ChunkProxy(ChunkManager chunks, UndoManager<World> undoManager)
    {
        this.chunks = chunks;
        this.undoManager = undoManager;
    }

    public ChunkManager getChunks()
    {
        return this.chunks;
    }

    public IBlockVariant getAir()
    {
        return this.getSet().air;
    }

    public BlockSet getSet()
    {
        return this.chunks.builder.models;
    }

    public boolean getMaskEnabled()
    {
        return this.maskEnabled;
    }

    public void setMaskEnabled(boolean maskEnabled)
    {
        this.maskEnabled = maskEnabled;
    }

    public List<IBlockVariant> getMask()
    {
        return this.mask;
    }

    public boolean isMaskEnabled()
    {
        return !this.mask.isEmpty() && this.maskEnabled;
    }

    public void begin()
    {
        this.recording = true;
    }

    public void end()
    {
        List<BlockDiff> list = new ArrayList<BlockDiff>();

        list.addAll(this.blocks.values());
        this.recording = false;
        this.blocks.clear();

        this.undoManager.pushUndo(createUndo(list));
    }

    protected BlocksUndo createUndo(List<BlockDiff> list)
    {
        return new BlocksUndo(list);
    }

    public boolean isBlockMasked(IBlockVariant variant)
    {
        boolean hasBlockInMask = false;

        for (IBlockVariant masked : this.mask)
        {
            if (masked.equals(variant))
            {
                hasBlockInMask = true;
            }
        }

        return hasBlockInMask;
    }

    public boolean setBlock(int x, int y, int z, IBlockVariant block)
    {
        IBlockVariant current = this.chunks.getBlock(x, y, z);

        if (this.isMaskEnabled() && !this.isBlockMasked(current))
        {
            return false;
        }

        return this.setBlockUnmasked(x, y, z, block);
    }

    public boolean setBlockUnmasked(int x, int y, int z, IBlockVariant block)
    {
        IBlockVariant current = this.chunks.getBlock(x, y, z);

        this.chunks.setBlockForced(x, y, z, block);

        if (this.recording)
        {
            Vector3i position = new Vector3i(x, y, z);
            BlockDiff diff = this.blocks.get(position);

            if (diff != null)
            {
                diff.current = block;
            }
            else
            {
                this.blocks.put(position, new BlockDiff(position, current, block));
            }
        }

        return !current.equals(block);
    }

    public IBlockVariant getBlock(int x, int y, int z)
    {
        return this.chunks.getBlock(x, y, z);
    }

    public boolean hasBlock(int x, int y, int z)
    {
        return this.chunks.hasBlock(x, y, z);
    }
}