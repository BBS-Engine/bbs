package mchorse.bbs.voxel.processor;

import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.undo.ChunkProxy;

public class CopyProcessor extends Processor
{
    private Chunk chunk;

    public CopyProcessor(Chunk chunk)
    {
        this.chunk = chunk;
    }

    @Override
    protected void processBlock(int x, int y, int z, ChunkProxy proxy)
    {
        this.chunk.setBlock(x - this.min.x, y - this.min.y, z - this.min.z, proxy.getBlock(x, y, z));
        this.chunk.setLighting(x - this.min.x, y - this.min.y, z - this.min.z, proxy.getChunks().getLighting(x, y, z));
    }
}