package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.voxel.storage.ChunkThread;
import mchorse.bbs.voxel.storage.ChunkView;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.world.WorldMetadata;
import org.joml.Vector3d;

public class ChunkColumnView extends ChunkView
{
    private Vector3d position = new Vector3d();

    public ChunkColumnView(ChunkArrayManager manager, ChunkThread thread, WorldMetadata metadata)
    {
        super(manager, thread, metadata);
    }

    @Override
    public ChunkThread getThread()
    {
        return this.thread;
    }

    @Override
    public void updateChunks(Vector3d vector)
    {
        /* Column view only cares about X and Z camera changes */
        this.position.set(vector);
        this.position.y = this.metadata.columnBase * this.manager.s;

        super.updateChunks(this.position);
    }
}