package mchorse.bbs.world;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Range;
import mchorse.bbs.voxel.generation.Generator;
import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.voxel.storage.column.ChunkColumnFactory;
import mchorse.bbs.voxel.storage.cubic.ChunkCubicFactory;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.io.File;

public class WorldMetadata implements IMapSerializable
{
    public static final Link TILESET = Link.assets("tileset/default.json");
    public final File save;

    /* Options */
    public String name = "";

    public long seed;
    public Link generator = Generator.DEFAULT;

    public int chunks = 24;
    public int chunkSize = 16;
    public boolean compress = true;
    public boolean column;

    public int columnBase = -2;
    public int columnHeight = 6;

    public Range limitX = new Range(0, 0);
    public Range limitY = new Range(0, 0);
    public Range limitZ = new Range(0, 0);

    /* Metadata */
    public final MapType metadata = new MapType();

    public static WorldMetadata fromFile(File worldFolder)
    {
        File metadataFile = new File(worldFolder, "metadata.json");

        if (!metadataFile.isFile())
        {
            return null;
        }

        WorldMetadata worldMetadata = new WorldMetadata(worldFolder);

        try
        {
            String code = IOUtils.readText(metadataFile);
            MapType metadata = DataToString.mapFromString(code);

            worldMetadata.fromData(metadata);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return worldMetadata;
    }

    public WorldMetadata(File save)
    {
        this.save = save;
    }

    public String getId()
    {
        return this.save == null ? "" : this.save.getName();
    }

    public ChunkFactory createFactory()
    {
        BlockSet blocks = this.createBlockSet();

        if (this.column)
        {
            return new ChunkColumnFactory(this.save, blocks, this);
        }

        return new ChunkCubicFactory(this.save, blocks, this);
    }

    public BlockSet createBlockSet()
    {
        BlockSet blocks = new BlockSet(TILESET);

        try
        {
            blocks.fromData(DataToString.mapFromString(IOUtils.readText(BBS.getProvider().getAsset(blocks.id))));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return blocks;
    }

    public Generator createGenerator()
    {
        return Generator.forName(this.generator);
    }

    @Override
    public void toData(MapType data)
    {
        data.combine(this.metadata);

        data.putString("name", this.name);

        data.putInt("seed", (int) this.seed);
        data.putString("generator", this.generator.toString());

        data.putInt("chunks", this.chunks);
        data.putInt("chunk_size", this.chunkSize);
        data.putBool("compress", this.compress);
        data.putBool("column", this.column);

        data.putInt("column_base", this.columnBase);
        data.putInt("column_height", this.columnHeight);

        data.remove("limit_x");
        data.remove("limit_y");
        data.remove("limit_z");

        if (this.limitX.enabled) data.putString("limit_x", this.limitX.stringify());
        if (this.limitY.enabled) data.putString("limit_y", this.limitY.stringify());
        if (this.limitZ.enabled) data.putString("limit_z", this.limitZ.stringify());
    }

    @Override
    public void fromData(MapType data)
    {
        this.metadata.combine(data);

        this.name = data.getString("name");

        this.seed = data.getLong("seed");
        this.generator = Link.create(data.getString("generator", Generator.DEFAULT.toString()));

        this.chunks = data.getInt("chunks", 24);
        this.chunkSize = data.getInt("chunk_size", 16);
        this.compress = data.getBool("compress", true);
        this.column = data.getBool("column");

        this.columnBase = data.getInt("column_base", -2);
        this.columnHeight = data.getInt("column_height", 6);

        if (data.has("limit_x")) this.limitX = Range.fromString(data.getString("limit_x"));
        if (data.has("limit_y")) this.limitY = Range.fromString(data.getString("limit_y"));
        if (data.has("limit_z")) this.limitZ = Range.fromString(data.getString("limit_z"));
    }
}