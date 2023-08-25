package mchorse.bbs.bridge;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.world.World;

public interface IBridgeWorld
{
    public World getWorld();

    public default ChunkBuilder getChunkBuilder()
    {
        return this.getWorld().chunks.builder;
    }

    public boolean loadWorld(String world);
}