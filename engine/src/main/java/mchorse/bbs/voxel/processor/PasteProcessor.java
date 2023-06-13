package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;
import org.joml.Vector3i;

public class PasteProcessor extends Processor
{
    private Chunk chunk;
    private boolean destroy;

    public PasteProcessor(Chunk chunk)
    {
        this(chunk, false);
    }

    public PasteProcessor(Chunk chunk, boolean destroy)
    {
        this.chunk = chunk;
        this.destroy = destroy;
    }

    public void process(Vector3i position, ChunkProxy proxy)
    {
        this.process(position, new Vector3i(position).add(this.chunk.w - 1, this.chunk.h - 1, this.chunk.d - 1), proxy);
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        IBlockVariant block = this.chunk.getBlock(x - this.min.x, y - this.min.y, z - this.min.z);

        if (!block.isAir())
        {
            proxy.setBlock(x, y, z, this.destroy ? proxy.getAir() : block);
        }
    }
}