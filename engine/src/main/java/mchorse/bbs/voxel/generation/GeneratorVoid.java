package mchorse.bbs.voxel.generation;

import mchorse.bbs.settings.values.ValueBlockLink;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;

public class GeneratorVoid extends Generator
{
    public ValueBlockLink platform = new ValueBlockLink("platform");

    public GeneratorVoid()
    {
        this.group.add(this.platform);
    }

    @Override
    public void generate(ChunkDisplay display, ChunkManager chunks)
    {
        if (display.x == 0 && display.y == 0 && display.z == 0)
        {
            ChunkCell cell = chunks.getCell(0, 0, 0, true);

            cell.setBlockLocal(0, 0, 0, this.platform.get(chunks.builder.models));
        }
    }
}