package mchorse.bbs.voxel.tilesets;

import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

import java.util.ArrayList;
import java.util.List;

public class BlockModels
{
    private BlockModelFactory factory;

    public BlockModels(BlockModelFactory factory)
    {
        this.factory = factory;
    }

    public final List<BlockModel> list = new ArrayList<>();

    public void add(int index, BlockModel model)
    {
        model.complete(this.factory);

        if (index == this.list.size())
        {
            this.list.add(model);
        }
        else if (index < this.list.size())
        {
            this.list.get(index).copy(model);
        }
        else
        {
            throw new IndexOutOfBoundsException("Given index " + index + " is out of range!");
        }
    }
}