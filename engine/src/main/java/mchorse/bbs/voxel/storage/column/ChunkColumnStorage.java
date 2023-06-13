package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChunkColumnStorage extends ChunkStorage
{
    public ChunkColumnStorage(File folder, WorldMetadata metadata)
    {
        super(folder, metadata);
    }

    @Override
    protected File getFile(ChunkCell cell)
    {
        int x = MathUtils.toChunk(cell.bounds.x, cell.manager.s);
        int z = MathUtils.toChunk(cell.bounds.z, cell.manager.s);

        return new File(this.folder, x + "." + z + ".dat");
    }

    @Override
    public List<ChunkCell> getCells(ChunkManager manager)
    {
        List<ChunkCell> cells = new ArrayList<ChunkCell>();

        for (File file : this.folder.listFiles())
        {
            String name = file.getName();

            if (!name.endsWith(".dat"))
            {
                continue;
            }

            String[] fragments = name.split("\\.");

            if (fragments.length == 3)
            {
                int x = Integer.parseInt(fragments[0]);
                int z = Integer.parseInt(fragments[1]);

                cells.add(manager.createCell(x, 0, z));
            }
        }

        return cells;
    }
}