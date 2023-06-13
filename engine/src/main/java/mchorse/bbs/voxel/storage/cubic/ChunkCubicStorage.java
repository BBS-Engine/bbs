package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.world.WorldMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChunkCubicStorage extends ChunkStorage
{
    public ChunkCubicStorage(File folder, WorldMetadata metadata)
    {
        super(folder, metadata);
    }

    @Override
    protected File getFile(ChunkCell cell)
    {
        int x = MathUtils.toChunk(cell.bounds.x, cell.manager.s);
        int y = MathUtils.toChunk(cell.bounds.y, cell.manager.s);
        int z = MathUtils.toChunk(cell.bounds.z, cell.manager.s);

        return new File(this.folder, x + "." + y + "." + z + ".dat");
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

            if (fragments.length == 4)
            {
                int x = Integer.parseInt(fragments[0]);
                int y = Integer.parseInt(fragments[1]);
                int z = Integer.parseInt(fragments[2]);

                cells.add(manager.createCell(x, y, z));
            }
        }

        return cells;
    }
}