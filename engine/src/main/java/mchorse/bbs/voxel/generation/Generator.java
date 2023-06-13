package mchorse.bbs.voxel.generation;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.WorldMetadata;

import java.util.List;
import java.util.Random;

public abstract class Generator
{
    public static final Link DEFAULT = Link.bbs("default");

    public long seed;
    public Random rand = new Random();

    protected ValueGroup group = new ValueGroup("generator");

    public static Generator forName(Link type)
    {
        Generator generator = BBS.getFactoryGenerators().create(type);

        if (generator == null)
        {
            generator = new GeneratorDefault();
        }

        return generator;
    }

    public void fromMetadata(WorldMetadata metadata, BlockSet blockSet)
    {
        this.seed = metadata.seed;

        for (BaseValue value : this.group.getAll())
        {
            BaseType type = metadata.metadata.get(value.getPath());

            if (type != null)
            {
                value.fromData(type);
            }
        }
    }

    public List<BaseValue> getValues()
    {
        return this.group.getAll();
    }

    public abstract void generate(ChunkDisplay display, ChunkManager chunks);
}